package com.study.travly.filter.item;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

	@Query("""
	        SELECT bfi.board.id, bfi.filterItem.name
	        FROM BoardFilterItem bfi
	        WHERE bfi.board.id IN :boardIds
	    """)
	    List<Object[]> findTagsByBoardIds(@Param("boardIds") List<Long> boardIds);
	
}
