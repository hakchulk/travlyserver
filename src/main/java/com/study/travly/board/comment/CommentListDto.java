package com.study.travly.board.comment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentListDto {
	private Long id;
	private String comment;
	private String memberId;
	private String nickname;
	private LocalDateTime updatedAt;
}
