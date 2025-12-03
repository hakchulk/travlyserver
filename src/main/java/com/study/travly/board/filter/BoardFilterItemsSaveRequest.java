package com.study.travly.board.filter;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardFilterItemsSaveRequest {
	private Long boardId;
	private List<Long> itemIds;
}