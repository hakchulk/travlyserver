package com.study.travly.board;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardSaveRequest {
	// Board fields
	private String title;
	private Long memberId; // Member 객체 대신 ID를 받음

	private List<Long> filterItemIds;

	// BoardPlace List fields (BoardPlace를 위한 DTO)
	private List<BoardPlaceDto> places;

	@Getter
	@Setter
	public static class BoardPlaceDto {
		private String title;
		private String content;
		private String mapPlaceId;
		private String externalId;
		private double x;
		private double y;

		// BoardPlaceFile List fields (BoardPlaceFile을 위한 DTO)
		private List<BoardPlaceFileDto> files;
	}

	@Getter
	@Setter
	public static class BoardPlaceFileDto {
		private Long fileId; // File 객체 대신 ID를 받음
	}
}
