package com.study.travly.board;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("board")
@RestController
public class BoardController {

	@Autowired
	BoardService boardService;

	@PostMapping
	Optional<Board> createBoard(@RequestBody BoardSaveRequest req) {
		return boardService.saveBoardWithAllDetails(req);
	}

	@Operation(summary = "특정 여행 기록 상세 조회", description = "board.id를 통해 단일 여행 기록을 조회하며, 조회수도 증가시킵니다.")
	@GetMapping("{id}")
	public Optional<Board> boardView(@PathVariable("id") Long id) {
		return boardService.findByIdWithPlaces(id);
	}

	@Operation(summary = "특정 여행 기록 전체 수정", description = "board.id를 통해 단일 여행 기록을 수정.")
	@PutMapping("{id}")
	public Optional<Board> boardUpdate(@PathVariable("id") Long id, @RequestBody BoardUpdateRequest req) {
		return boardService.updateBoardWithAllDetails(id, req);
	}

	@GetMapping("member/{memberId}")
	Page<BoardListResponse> getBoardListByMemberId(@PathVariable(name = "memberId") Long memberId,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page) {
		Pageable p = PageRequest.of(page, size);
		return boardService.getBoardListByMemberId(memberId, p);
	}

	@GetMapping("member/{memberId}/bookmark")
	Page<BoardListResponse> getBookmarkBoardList(@PathVariable(name = "memberId") Long memberId,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page) {
		Pageable p = PageRequest.of(page, size);
		return boardService.getBookmarkBoardList(memberId, p);
	}

	@PostMapping("search")
	Page<BoardListResponse> getBoardList(@RequestBody(required = true) BoardListRequest req,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page) {
		Pageable p = PageRequest.of(page, size);

		return boardService.getBoardList(req, p);
	}

	@Operation(summary = "게시글 전체 목록 조회(최신순)", description = "게시글 전체 목록을 페이지 단위로 조회합니다. 기본 정렬은 updatedAt 내림차순(최신순)입니다.")
	@GetMapping
	Page<BoardListResponse> getBoardList(@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "orderby", defaultValue = "updated") String orderby) {
		Pageable p = PageRequest.of(page, size);

		return boardService.getBoardListAll(p, orderby);
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Long id) {
		boardService.delete(id);
	}
}
