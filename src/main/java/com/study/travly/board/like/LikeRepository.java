package com.study.travly.board.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LikeRepository extends JpaRepository<Like, Long> {

	Optional<Like> findByBoardIdAndMemberId(Long boardId, Long memberId);

	void deleteByBoardIdAndMemberId(Long boardId, Long memberId);

    long countByBoardId(Long boardId);
}