package com.study.travly.board;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
	Page<BoardListResponse> findBoardList(List<Long> itemIds, Pageable pageable);
}
