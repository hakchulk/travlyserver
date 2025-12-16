package com.study.travly.board;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
	@Query("""
			SELECT COUNT(l) FROM Like l WHERE l.board.id = :boardID
			""")
	Long getLikeCountByBoardId(@Param("boardID") Long boardID);

	@Query("SELECT DISTINCT b FROM Board b JOIN FETCH b.places p JOIN FETCH b.member m JOIN FETCH p.files f WHERE b.id = :boardId")
	Optional<Board> findByIdWithPlaces(@Param("boardId") Long boardId);

	Optional<Board> findById(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :boardID")
	int incrementViewCount(@Param("boardID") Long boardID);

	@Modifying // DML (DELETE) 쿼리임을 명시
	@Query("DELETE FROM BoardPlace bp WHERE bp.board.id = :boardId")
	int deleteBoardPlaceByBoardId(@Param("boardId") Long boardId);

}