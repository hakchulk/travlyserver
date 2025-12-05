package com.study.travly.board;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class BoardController {

	@Autowired
	BoardService boardService;

	@PostMapping("board")
	Optional<Board> createBoard(@RequestBody BoardSaveRequest req) {
		return boardService.saveBoardWithAllDetails(req);
	}

	@Operation(summary = "특정 여행 기록 상세 조회", description = "board.id를 통해 단일 여행 기록을 조회하며, 조회수도 증가시킵니다.")
	@GetMapping("board/{id}")
	public Optional<Board> boardListView(@PathVariable("id") Long id) {
		return boardService.findByIdWithPlaces(id);
	}

}
