package com.study.travly.board;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BoardListResponse {
	private Long id;
	private String title;
	private Long placeId;
	private String placeTitle;
	private String placeFilename;
	private LocalDateTime updatedAt;
	private List<String> filterItemNames; // 후처리에서 채움
	private Long memberId;
	private String memberNickname;
	private Long badgeId;
	private Long likeCount; // 후처리에서 채움

	public BoardListResponse(Long id, String title, Long placeId, String placeTitle, String placeFilename,
			LocalDateTime updatedAt, Long memberId, String memberNickname, Long badgeId) {
		this.id = id;
		this.title = title;
		this.placeId = placeId;
		this.placeTitle = placeTitle;
		this.placeFilename = placeFilename;
		this.updatedAt = updatedAt;
		this.memberId = memberId;
		this.memberNickname = memberNickname;
		this.badgeId = badgeId;
	}

}
