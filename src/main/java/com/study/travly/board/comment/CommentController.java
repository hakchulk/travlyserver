package com.study.travly.board.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "댓글 API", description = "게시글 댓글 조회 및 관리")
@RestController
// 일반적으로 부모 리소스의 하위 경로로 설정합니다. 예: /api/boards/{boardId}/comments

public class CommentController {
	@Autowired
	private CommentService commentService;

	@Operation(summary = "게시글 댓글 목록 페이징 조회 (최신순)", description = "특정 게시글 ID에 연결된 댓글 목록을 페이지 단위로 조회합니다. 기본 정렬은 updatedAt 내림차순(최신순)입니다.")
	@GetMapping("boardcomment")
	public Page<CommentListDto> getComments(
			@Parameter(description = "댓글을 조회할 게시글의 고유 ID") @RequestParam("boardId") Long boardId,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page) {

		Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
		return commentService.getCommentListByBoardId(boardId, p);
	}

}