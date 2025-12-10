package com.study.travly.board.comment;

import lombok.Getter;

@Getter
public class CommentRequest {
	private Long boardId;
	private Long memberId;
	private String comment;
}
