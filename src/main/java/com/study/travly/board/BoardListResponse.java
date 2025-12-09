package com.study.travly.board;

import java.time.LocalDateTime;
import java.util.List;

public class BoardListResponse {
	private Long id; // boardId
	private String title;
	private Long placeId; // 첫번쨰 place의 id
	private String placeTitle;
	private String placeFilename;
	private LocalDateTime updatedAt;
	private List<String> filterItemNames;
	private Long memberId;
	private String memberNickname;
	private Long badgeId;
	private int likeCount; // 좋아요 갯수
}
