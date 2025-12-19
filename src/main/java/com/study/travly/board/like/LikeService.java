package com.study.travly.board.like;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.study.travly.board.Board;
import com.study.travly.board.BoardRepository;
import com.study.travly.exception.BadRequestException;
import com.study.travly.member.Member;
import com.study.travly.member.MemberRepository;

import jakarta.transaction.Transactional;

@Service
public class LikeService {
	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Transactional
	public boolean toggleLike(Long memberId, Long boardId) {
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 board.id [%d]", boardId)));

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 member.id [%d]", memberId)));

		Optional<Like> existingLike = likeRepository.findByBoardIdAndMemberId(boardId, memberId);

		// 있으면 삭제
		if (existingLike.isPresent()) {
			likeRepository.deleteByBoardIdAndMemberId(boardId, memberId);
			return false;
		}
		//없으면 생성
		else {
			Like newLike = new Like(null, board, member, null);

			likeRepository.save(newLike);
			return true;
		}

	}

	public boolean IsLiked(Long memberId, Long boardId) {
		boardRepository.findById(boardId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 board.id [%d]", boardId)));

		memberRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 member.id [%d]", memberId)));

		return likeRepository.existsByBoardIdAndMemberId(boardId, memberId);
	}

}