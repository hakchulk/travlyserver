package com.study.travly.board;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

	private final EntityManager em;
	private final String jpqlBase = """
			SELECT distinct new com.study.travly.board.BoardListResponse(
				b.id, b.title, bp.id, bp.title, bpf.id, b.updatedAt, m.id, m.nickname, m.badge.id, b.viewCount, f.filename
				, (SELECT COUNT(l) FROM Like l WHERE l.board.id = b.id) as likeCount
			)
			FROM Board b
			JOIN b.member m
			LEFT JOIN b.places bp ON bp.orderNum = 0
			LEFT JOIN bp.files bpf ON bpf.orderNum = 0
			left join bpf.file f
			""";

	@Override
	public Page<BoardListResponse> findBoardListOrderByLikes(Pageable pageable) {
		String jpql = jpqlBase + """
				ORDER BY likeCount DESC, b.updatedAt DESC
				""";

		String countJpql = """
				SELECT COUNT(DISTINCT b.id) FROM Board b
				""";

		return findBoardList(jpql, countJpql, pageable);
	}

	@Override
	public Page<BoardListResponse> findBoardListByMemberId(Long memberId, Pageable pageable) {
		String jpql = jpqlBase + """
				where m.id = :memberId
				ORDER BY b.updatedAt DESC
				""";

		String countJpql = """
				SELECT COUNT(DISTINCT b.id) FROM Board b
				WHERE b.member.id = :memberId
				""";

		return findBoardList(memberId, jpql, countJpql, pageable);
	}

	public Page<BoardListResponse> findBoardList(Long memberId, String jpql, String countJpql, Pageable pageable) {
		TypedQuery<BoardListResponse> query = em.createQuery(jpql, BoardListResponse.class);
		query.setParameter("memberId", memberId);
		TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
		countQuery.setParameter("memberId", memberId);

		Long total = countQuery.getSingleResult();

		return findBoardList(query, pageable, total);
	}

	public Page<BoardListResponse> findBoardList(String jpql, String countJpql, Pageable pageable) {
		TypedQuery<BoardListResponse> query = em.createQuery(jpql, BoardListResponse.class);
		TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

		Long total = countQuery.getSingleResult();
		return findBoardList(query, pageable, total);
	}

	@Override
	public Page<BoardListResponse> findBookmarkBoardList(Long memberId, Pageable pageable) {
		String jpql = jpqlBase + """
				join Bookmark bm on bm.board.id = b.id
				where bm.member.id = :memberId
				ORDER BY b.updatedAt DESC
				""";

		String countJpql = """
				SELECT COUNT(DISTINCT b.id) FROM Board b
				JOIN b.member m
				join Bookmark bm on bm.board.id = b.id
				WHERE bm.member.id = :memberId """;

		return findBoardList(memberId, jpql, countJpql, pageable);
	}

	private long getTotalCount(List<Long> itemIds) {
		// 페이징 처리 위한 count 쿼리
		String countJpql = "SELECT COUNT(DISTINCT b.id) FROM Board b " + (itemIds != null && !itemIds.isEmpty()
				? "JOIN BoardFilterItem bi ON bi.board.id = b.id WHERE bi.filterItem.id IN :itemIds "
				: "");

		TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

		if (itemIds != null && !itemIds.isEmpty()) {
			countQuery.setParameter("itemIds", itemIds);
		}

		return countQuery.getSingleResult();
	}

	@Override
	public Page<BoardListResponse> findBoardList(List<Long> itemIds, Pageable pageable) {
		String jpql = jpqlBase + (itemIds != null && !itemIds.isEmpty() ? """
				JOIN BoardFilterItem bi ON bi.board.id = b.id
				WHERE bi.filterItem.id IN :itemIds
				""" : "") + "ORDER BY b.updatedAt DESC";

		TypedQuery<BoardListResponse> query = em.createQuery(jpql, BoardListResponse.class);

		if (itemIds != null && !itemIds.isEmpty()) {
			query.setParameter("itemIds", itemIds);
		}

		Long total = getTotalCount(itemIds);

		return findBoardList(query, pageable, total);
	}

	public Page<BoardListResponse> findBoardList(TypedQuery<BoardListResponse> query, Pageable pageable, long total) {

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		List<BoardListResponse> content = query.getResultList();

		// ✅ 3) BoardFilterItem 조회 → filterItemNames 구성
		List<Long> boardIds = content.stream().map(BoardListResponse::getId).toList();

		if (!boardIds.isEmpty()) {
			List<Object[]> filterRows = em.createQuery("""
					SELECT bi.board.id, i.name
						FROM BoardFilterItem bi
						JOIN bi.filterItem i
						WHERE bi.board.id IN :ids
					""", Object[].class).setParameter("ids", boardIds).getResultList();

			Map<Long, List<String>> filterMap = filterRows.stream().collect(Collectors.groupingBy(row -> (Long) row[0], // key 
					Collectors.mapping(row -> (String) row[1], Collectors.toList())));

			content.forEach(res -> {
				List<String> names = filterMap.get(res.getId());
				res.setFilterItemNames(names == null ? List.of() : names);
			});
		}

		return new PageImpl<>(content, pageable, total);

	}
}