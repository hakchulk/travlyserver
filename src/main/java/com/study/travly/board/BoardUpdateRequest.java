package com.study.travly.board;

import java.util.List;

import com.study.travly.board.place.BoardPlaceDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateRequest {
	// Board fields
	private String title;

	// memberId 는 변경 하지 않는다.

	private List<Long> filterItemIds;

	// BoardPlace List fields (BoardPlace를 위한 DTO)
	private List<BoardPlaceDto> places;

	@Getter
	@Setter
	public static class BoardPlaceFileDto {
		private Long fileId; // File 객체 대신 ID를 받음
	}
}
