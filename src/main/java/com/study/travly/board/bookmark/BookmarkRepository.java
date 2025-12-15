package com.study.travly.board.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	boolean existsByBoardIdAndMemberId(Long boardId, Long memberId);

	@Modifying // DML (DELETE) 쿼리임을 명시
	@Query("DELETE FROM Bookmark b WHERE b.board.id = :boardId AND b.member.id = :memberId")
	int deleteByBoardIdAndMemberId(@Param("boardId") Long boardId, @Param("memberId") Long memberId);
}
