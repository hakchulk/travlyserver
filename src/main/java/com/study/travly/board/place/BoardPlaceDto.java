package com.study.travly.board.place;

import java.util.List;

import com.study.travly.board.BoardSaveRequest.BoardPlaceFileDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardPlaceDto {
	private String title;
	private String content;
	private String mapPlaceId;
	private String externalId;
	private double x;
	private double y;

	// BoardPlaceFile List fields (BoardPlaceFile을 위한 DTO)
	private List<BoardPlaceFileDto> files;
}
