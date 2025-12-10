package com.study.travly.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.travly.board.comment.CommentListDto;
import com.study.travly.board.comment.CommentService;
import com.study.travly.exception.BadRequestException;

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
	@GetMapping("{memberId}/comment")
	public Page<CommentListDto> getComments(
			@Parameter(description = "댓글을 조회할 member.id") @PathVariable("memberId") Long memberId,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page) {

		Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

		return commentService.getCommentListByMemberId(memberId, p);
	}

	record IsExistResponse(boolean isExist) {
	}

	/**
	 * 이메일 또는 닉네임 중복 여부를 확인하는 통합 API
	 * GET check?email=test@example.com
	 * GET check?nickname=testuser
	 * * @param email 확인할 이메일 (Optional)
	 * @param nickname 확인할 닉네임 (Optional)
	 * @return 항목의 존재 유무(true/false)를 담은 ResponseEntity
	 */
	@GetMapping("/check")
	public IsExistResponse checkExistence(@RequestParam(name = "email", required = false) String email,
			@RequestParam(name = "nickname", required = false) String nickname) {

		// 1. 요청 파라미터 검증 (두 값 모두 없거나, 두 값 모두 있을 때 예외 처리)
		if ((email == null && nickname == null) || (email != null && nickname != null)) {
			// 400 Bad Request
			throw new BadRequestException("파라미터 'nickname' 또는 'email' 가 사용 되어야 합니다.");
		}

		return new IsExistResponse(memberService.checkExistence(email, nickname));
	}
}
