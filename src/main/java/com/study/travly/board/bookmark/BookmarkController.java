package com.study.travly.board.bookmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.travly.auth.CustomUserPrincipal;

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

}
