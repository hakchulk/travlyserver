package com.study.travly.board.comment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentListDto {
	private Long id;
	private String comment;
	private Long memberId;
	private Long boardId;
	private String nickname;
	private String profileImageFilename;
	private Long badgeId;
	private LocalDateTime updatedAt;
}
