package com.study.travly.board.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.travly.board.Board;
import com.study.travly.board.BoardRepository;
import com.study.travly.exception.BadRequestException;
import com.study.travly.member.Member;
import com.study.travly.member.MemberRepository;

@Service
@Transactional(readOnly = true)
public class CommentService {
	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private MemberRepository memberRepository; // Member 조회용

	/**
	 * 특정 게시글(boardId)에 속한 댓글 목록을 페이징 처리하고 DTO로 변환하여 반환합니다.
	 * @param boardId 댓글을 조회할 게시글의 ID
	 * @param pageable 페이징 정보 (페이지 번호, 페이지 크기, 정렬 정보 등)
	 * @return CommentListDto의 페이징된 목록 (Page<CommentListDto>)
	 */
	public Page<CommentListDto> getCommentListByBoardId(Long boardId, Pageable pageable) {
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 board.id [%d]", boardId)));

		// 1. Repository를 통해 페이징된 Comment 엔티티 목록을 조회합니다.
		Page<Comment> commentPage = commentRepository.findByBoardId(boardId, pageable);

		return commentPage.map(comment -> {
			return comment2CommentListDto(comment);
		});
	}

	private CommentListDto comment2CommentListDto(Comment comment) {
		CommentListDto dto = new CommentListDto();
		dto.setId(comment.getId());
		dto.setComment(comment.getComment());
		dto.setMemberId(comment.getMember().getId());
		dto.setBoardId(comment.getBoard().getId());
		dto.setNickname(comment.getMember().getNickname());
		dto.setUpdatedAt(comment.getUpdatedAt());
		return dto;
	}

	@Transactional
	public Page<CommentListDto> getCommentListByMemberId(Long memberId, Pageable pageable) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 member.id [%d]", memberId)));

		// 1. Repository를 통해 페이징된 Comment 엔티티 목록을 조회합니다.
		Page<Comment> commentPage = commentRepository.findByMemberId(memberId, pageable);
		memberRepository.initNotificationCount(memberId);
		return commentPage.map(comment -> {
			return comment2CommentListDto(comment);
		});
	}

	@Transactional
	public CommentResponse create(CommentRequest req) {
		Member member = memberRepository.findById(req.getMemberId())
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 member.id [%d]", req.getMemberId())));

		Board board = boardRepository.findById(req.getBoardId())
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 board.id [%d]", req.getBoardId())));

		boolean b = commentRepository.existsByBoardIdAndMemberId(req.getBoardId(), req.getMemberId());
		if (b)
			throw new BadRequestException(String.format("Comment 에 board.id [%d], member.id [%d] 가 이미 존재 합니다.",
					req.getBoardId(), req.getMemberId()));

		Comment comment = new Comment(null, board, member, req.getComment(), null, null);
		commentRepository.save(comment);
		CommentResponse ret = new CommentResponse(comment.getId(), req.getBoardId(), req.getMemberId(),
				req.getComment(), comment.getCreatedAt());

		// 멤버의 알림을 증가시킨다.
		memberRepository.incrementNotificationCount(board.getMember().getId());
		return ret;
	}

	@Transactional
	public void delete(Long commentId) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 comment.id [%d]", commentId)));

		commentRepository.delete(comment);
	}

}