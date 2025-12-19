package com.study.travly.board.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface LikeRepository extends JpaRepository<Like, Long> {

	Optional<Like> findByBoardIdAndMemberId(Long boardId, Long memberId);

	@Modifying
	@Transactional
	void deleteByBoardIdAndMemberId(Long boardId, Long memberId);

	boolean existsByBoardIdAndMemberId(Long boardId, Long memberId);

}