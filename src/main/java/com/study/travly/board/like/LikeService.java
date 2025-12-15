package com.study.travly.board.like;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.study.travly.board.Board;
import com.study.travly.board.BoardRepository;
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
		Optional<Like> existingLike = likeRepository.findByBoardIdAndMemberId(boardId, memberId);

		
		// 있으면 삭제
		if (existingLike.isPresent()) {
			likeRepository.deleteByBoardIdAndMemberId(boardId, memberId);
			return false;
		} 
		
		//없으면 생성
		else {
		    Board board = boardRepository.findById(boardId)
		            .orElseThrow(() -> new IllegalArgumentException("게시물 ID 찾을 수 없음"));
		    Member member = memberRepository.findById(memberId)
		            .orElseThrow(() -> new IllegalArgumentException("사용자 ID 찾을 수 없음"));
		    
		    Like newLike = new Like(null, member, board, null);
		    
		    likeRepository.save(newLike);
		    return true;
		}

	}

}