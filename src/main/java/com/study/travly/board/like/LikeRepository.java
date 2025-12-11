package com.study.travly.board.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT COUNT(l) FROM Like l WHERE l.board.id = :boardId")
    int countByBoardId(@Param("boardId") Long boardId);
    
    //좋아요 존재 여부 확인
    @Query("""
    		SELECT l
    		FROM Like l
    		WHERE l.board.id = :boardId
    		AND l.member.id = :memberId
    		""")
    Optional<Like> findLikeByBoardAndMember(
    		@Param("boardId") Long boardId,
    		@Param("memberId") Long memberId
    		);
    
    // 좋아요 개수 카운트
    @Query("""
    		SELECT COUNT(l)
    		FROM Like l
    		WHERE l.boardid = :boardId
    		""")
    Long countLikesByBoardId(@Param("boardId") Long boardId);
    
    // 좋아요 빼기(삭제)
    @Modifying
    @Query("""
    		DELETE
    		FROM Like l
    		WHERE l.board.id = :boardId
    		AND l.member.id = :memberId
    		""")
    void deleteLikeByBoardAndMember(
    		@Param("boardId") Long boardId,
    		@Param("memberId") Long memberId
    		);
}
