package com.study.travly.board.bookmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookmarkController {
	@Autowired
	BookmarkService bookmarkService;

	@PostMapping("board/{boardId}/bookmark")
	Bookmark create(@PathVariable("boardId") Long boardId, @RequestParam("memberId") Long memberId) {
		return bookmarkService.create(boardId, memberId);
	}

	@DeleteMapping("bookmark")
	void delete(@RequestParam("boardId") Long boardId, @RequestParam("memberId") Long memberId) {
		bookmarkService.delete(boardId, memberId);
	}

}
