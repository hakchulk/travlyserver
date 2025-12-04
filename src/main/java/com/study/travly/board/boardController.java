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
public class boardController {

	@Autowired
	BoardService boardService;

	/*
	{
	  "title": "제주도 2박 3일 힐링 여행 일정",
	  "memberId": 2, 
	  "filterItemIds": [4,5,7],
	  "places": [
	{
	  "title": "첫째 날: 협재 해수욕장",
	  "content": "물이 맑고 에메랄드 빛이 아름다운 해변입니다. 일몰이 특히 멋져요.",
	  "mapPlaceId": "KakaoMap_12345",
	  "externalId": "TourAPI_6789",
	  "x": 126.2415, 
	  "y": 33.3982, 
	  "files": [    {
	      "fileId": 1
	    },
	    {
	      "fileId": 2
	    }
	  ]
	},
	{
	  "title": "둘째 날: 성산일출봉",
	  "content": "일출을 보기 위해 새벽에 올랐습니다. 정상에서의 뷰는 정말 최고였어요.",
	  "mapPlaceId": "KakaoMap_98765",
	  "externalId": null,
	  "x": 126.9365,
	  "y": 33.4623,
	  "files": [
	    {
	      "fileId": 3
	    }
	  ]
	}
	
	  ]
	} 
	 */
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
