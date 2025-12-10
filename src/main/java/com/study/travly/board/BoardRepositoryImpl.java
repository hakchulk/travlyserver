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
		String jpql = """
				SELECT distinct new com.study.travly.board.BoardListResponse(
					b.id, b.title, bp.id, bp.title, f.filename, b.updatedAt, m.id, m.nickname, m.badge.id
				)
				FROM Board b
				JOIN b.member m
				LEFT JOIN b.places bp ON bp.orderNum = 0
				LEFT JOIN bp.files bpf ON bpf.orderNum = 0
				left join bpf.file f
				""" + (itemIds != null && !itemIds.isEmpty() ? """
				JOIN BoardFilterItem bi ON bi.board.id = b.id
				WHERE bi.filterItem.id IN :itemIds
				""" : "") + "ORDER BY b.updatedAt DESC";

		TypedQuery<BoardListResponse> query = em.createQuery(jpql, BoardListResponse.class);

		if (itemIds != null && !itemIds.isEmpty()) {
			query.setParameter("itemIds", itemIds);
		}

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		List<BoardListResponse> content = query.getResultList();

		// 페이징 처리 위한 count 쿼리
		Long total = getTotalCount(itemIds);

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

		// ✅ 4) Like 조회 → likeCount 구성
		if (!boardIds.isEmpty()) {
			List<Object[]> likeRows = em.createQuery("SELECT l.board.id, COUNT(l.id) " + "FROM Like l "
					+ "WHERE l.board.id IN :ids " + "GROUP BY l.board.id", Object[].class).setParameter("ids", boardIds)
					.getResultList();

			Map<Long, Long> likeMap = likeRows.stream()
					.collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

			content.forEach(res -> {
				Long count = likeMap.get(res.getId());
				res.setLikeCount(count == null ? 0L : count);
			});
		}

		return new PageImpl<>(content, pageable, total);

	}
}