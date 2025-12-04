package com.study.travly.board.comment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;

	/**
	 * 특정 게시글(boardId)에 속한 모든 댓글을 조회합니다.
	 * @param boardId 댓글을 조회할 게시글의 ID
	 * @return 해당 게시글의 댓글 목록 (List<Comment>)
	 */
	public List<Comment> getCommentsByBoardId(Long boardId) {
		return commentRepository.findByBoardIdOrderByUpdatedAtDesc(boardId);

	}

	/**
	 * 특정 게시글(boardId)에 속한 댓글 목록을 페이징 처리하고 DTO로 변환하여 반환합니다.
	 * @param boardId 댓글을 조회할 게시글의 ID
	 * @param pageable 페이징 정보 (페이지 번호, 페이지 크기, 정렬 정보 등)
	 * @return CommentListDto의 페이징된 목록 (Page<CommentListDto>)
	 */
	@Transactional
	public Page<CommentListDto> getCommentListByBoardId(Long boardId, Pageable pageable) {

		// 1. Repository를 통해 페이징된 Comment 엔티티 목록을 조회합니다.
		Page<Comment> commentPage = commentRepository.findByBoardId(boardId, pageable);

		// 2. Page<Comment>를 Page<CommentListDto>로 변환합니다.
		// Page가 제공하는 map() 메서드를 사용하면 쉽게 변환할 수 있으며, 
		// Page의 메타 정보(전체 개수, 페이지 수 등)는 자동으로 유지됩니다.
		return commentPage.map(comment -> {
			CommentListDto dto = new CommentListDto();
			dto.setId(comment.getId());
			dto.setComment(comment.getComment());

			// Member 정보를 DTO 필드에 맞게 매핑 (Member 엔티티에 getNickname()이 있다고 가정)
			if (comment.getMember() != null) {
				dto.setMemberId(String.valueOf(comment.getMember().getId()));
				dto.setNickname(comment.getMember().getNickname());
			}

			dto.setUpdatedAt(comment.getUpdatedAt());
			return dto;
		});
	}
}