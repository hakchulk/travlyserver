package com.study.travly.board.bookmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.travly.auth.CustomUserPrincipal;

record BookmarkResponse(boolean isBookmarked) {

}

@RestController
public class BookmarkController {
	@Autowired
	BookmarkService bookmarkService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("board/{boardId}/bookmark")
	Bookmark create(@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable("boardId") Long boardId) {
		return bookmarkService.create(boardId, principal.getMemberId());
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("bookmark")
	void delete(@AuthenticationPrincipal CustomUserPrincipal principal, @RequestParam("boardId") Long boardId) {
		bookmarkService.delete(boardId, principal.getMemberId());
	}

	@GetMapping("board/{boardId}/bookmark")
	@PreAuthorize("isAuthenticated()")
	public BookmarkResponse IsBookmarked(@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable("boardId") Long boardId) {

		Long memberId = principal.getMemberId();

		// 좋아요 토글 로직 실행 (Service 계층 위임)
		boolean b = bookmarkService.IsBookmarked(boardId, memberId);
		return new BookmarkResponse(b);
	}
}
