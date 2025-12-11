package com.study.travly.board;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class BoardListResponse {
	private Long id;
	private String title;
	private Long placeId;
	private String placeTitle;
	private Long placeFileId;
	private LocalDateTime updatedAt;
	private Long memberId;
	private String memberNickname;
	private Long badgeId;
	private Integer viewCount;
	private String thmbnailFilename;
	private Long likeCount; // 후처리에서 채움
	private List<String> filterItemNames; // 후처리에서 채움

	public BoardListResponse(Long id, String title, Long placeId, String placeTitle, Long placeFileId,
			LocalDateTime updatedAt, Long memberId, String memberNickname, Long badgeId, Integer viewCount,
			String filename) {
		this.id = id;
		this.title = title;
		this.placeId = placeId;
		this.placeTitle = placeTitle;
		this.placeFileId = placeFileId;
		this.updatedAt = updatedAt;
		this.memberId = memberId;
		this.memberNickname = memberNickname;
		this.badgeId = badgeId;
		this.viewCount = viewCount;
		if (filename != null)
			this.thmbnailFilename = "t_" + filename + ".jpg";
	}

	public BoardListResponse(Long id, String title, Long placeId, String placeTitle, Long placeFileId,
			LocalDateTime updatedAt, Long memberId, String memberNickname, Long badgeId, Integer viewCount,
			String filename, Long likeCount) {
		this.id = id;
		this.title = title;
		this.placeId = placeId;
		this.placeTitle = placeTitle;
		this.placeFileId = placeFileId;
		this.updatedAt = updatedAt;
		this.memberId = memberId;
		this.memberNickname = memberNickname;
		this.badgeId = badgeId;
		this.viewCount = viewCount;
		if (filename != null)
			this.thmbnailFilename = "t_" + filename + ".jpg";
		this.likeCount = likeCount;
	}
}
