package com.study.travly.board.filter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardFilterItemRepository extends JpaRepository<BoardFilterItem, Long> {

	/**
	 * 특정 Board ID와 연결된 모든 BoardFilterItem 관계를 삭제합니다.
	 */
	void deleteByBoardId(Long boardId);

	@Query("""
			SELECT bfi FROM BoardFilterItem bfi
			      WHERE bfi.board.id = :boardId
			      ORDER BY bfi.filterItem.id ASC
			""")
	List<BoardFilterItem> findAllByBoardIdOrderByFilterItemId(@Param("boardId") Long boardId);

}