package com.study.travly.board.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.travly.auth.CustomUserPrincipal;
import com.study.travly.board.BoardRepository;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

record LikeResponse(boolean isLiked) {
}

@Slf4j
@RestController
@RequestMapping("board")
@AllArgsConstructor
public class LikeController {

	@Autowired
	LikeService likeService;

	@Autowired
	BoardRepository boardRepository;

	@Operation(summary = "게시물 좋아요/취소 토글", description = "인증된 사용자가 특정 게시물에 좋아요를 등록하거나 이미 등록된 좋아요를 취소합니다.")
	@PostMapping("/{boardId}/like")
	@PreAuthorize("isAuthenticated()")
	public LikeResponse toggleLike(@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable("boardId") Long boardId) {

		Long memberId = principal.getMemberId();
		log.info("요청 memberId 확인: ", memberId);

		// 좋아요 토글 로직 실행 (Service 계층 위임)
		boolean isLiked = likeService.toggleLike(memberId, boardId);
		return new LikeResponse(isLiked);
	}

}