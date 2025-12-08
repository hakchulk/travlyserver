package com.study.travly.board.comment;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {
	private Long id;
	private Long boardId;
	private Long memberId;
	private String comment;
	private LocalDateTime createdAt;
}
