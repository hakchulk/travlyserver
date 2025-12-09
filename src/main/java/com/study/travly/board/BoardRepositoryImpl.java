package com.study.travly.board;

import java.util.List;

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

	@Override
	public Page<BoardListResponse> findBoardList(List<Long> itemIds, Pageable pageable) {

		String jpql = "SELECT new com.study.travly.board.BoardListResponse("
				+ "b.id, b.title, bp.id, bp.title, f.filename, b.updatedAt, "
				+ "i.name, m.id, m.nickname, m.badge.id, COUNT(l.id)) " + "FROM Board b " + "JOIN b.member m "
				+ "LEFT JOIN b.places bp ON bp.orderNum = 1 " + "LEFT JOIN bp.files f ON f.orderNum = 1 "
				+ "LEFT JOIN Like l ON l.board.id = b.id " + "LEFT JOIN BoardFilterItem bi ON bi.board.id = b.id "
				+ "LEFT JOIN FilterItem i ON i.id = bi.item.id "
				+ (itemIds != null && !itemIds.isEmpty() ? "WHERE i.id IN :itemIds " : "")
				+ "GROUP BY b.id, bp.id, f.filename, m.id, m.badge.id";

		TypedQuery<BoardListResponse> query = em.createQuery(jpql, BoardListResponse.class);

		if (itemIds != null && !itemIds.isEmpty()) {
			query.setParameter("itemIds", itemIds);
		}

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		List<BoardListResponse> content = query.getResultList();

		// count query
		String countJpql = "SELECT COUNT(DISTINCT b.id) FROM Board b "
				+ "LEFT JOIN BoardFilterItem bi ON bi.board.id = b.id "
				+ (itemIds != null && !itemIds.isEmpty() ? "WHERE bi.item.id IN :itemIds" : "");

		TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

		if (itemIds != null && !itemIds.isEmpty()) {
			countQuery.setParameter("itemIds", itemIds);
		}

		Long total = countQuery.getSingleResult();

		return new PageImpl<>(content, pageable, total);
	}
}