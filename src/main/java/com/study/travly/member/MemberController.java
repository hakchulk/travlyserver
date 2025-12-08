package com.study.travly.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.travly.board.comment.CommentListDto;
import com.study.travly.board.comment.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("member")
public class MemberController {
	@Autowired
	MemberService memberService;
	@Autowired
	private CommentService commentService;

	@PostMapping
	public Member modifyCreateMember(@RequestBody MemberModifyRequest req) {
		return memberService.modifyCreateMember(req);
	}

	@Operation(summary = "게시글 댓글 목록 페이징 조회 (최신순)", description = "member.id 로 댓글 목록을 페이지 단위로 조회합니다. 기본 정렬은 updatedAt 내림차순(최신순)입니다.")
	@GetMapping("comment")
	public Page<CommentListDto> getComments(
			@Parameter(description = "댓글을 조회할 member.id") @RequestParam("memberId") Long memberId,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page) {

		Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

		return commentService.getCommentListByMemberId(memberId, p);
	}
}
