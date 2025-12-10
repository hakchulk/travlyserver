package com.study.travly.board;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardListRequest {
	List<Long> itemIds;
}
