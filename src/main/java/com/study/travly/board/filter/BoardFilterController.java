package com.study.travly.board.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoardFilterController {

	@Autowired
	BoardFilterService boardFilterService;

	/*
	 * 예) { "boardId": 7, "itemIds": [4,5,7] }
	 */
	@PostMapping("boardfilter")
	public void saveBoardFilters(@RequestBody BoardFilterItemsSaveRequest req) {
		boardFilterService.saveBoardFilterItems(req);
	}

	@GetMapping("boardfilter/{board_id}")
	public List<BoardFilterItemResponseDto> getBoardfilters(@PathVariable("board_id") Long board_id) {
		return boardFilterService.findByBoardId(board_id);
	}
}
