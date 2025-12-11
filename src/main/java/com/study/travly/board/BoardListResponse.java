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
	private String placeContent;
	private Long placeFileId;
	private LocalDateTime updatedAt;
	private Long memberId;
	private String memberNickname;
	private String memberThumbail;
	private Long badgeId;
	private Integer viewCount;
	private String thumbnailFilename;
	private Long likeCount; // 후처리에서 채움
	private List<String> filterItemNames; // 후처리에서 채움

	public BoardListResponse(Long id, String title, Long placeId, String placeTitle, Long placeFileId,
			LocalDateTime updatedAt, Long memberId, String memberNickname, Long badgeId, Integer viewCount,
			String filename, Long likeCount, String memberProfileFilename, String placeContent) {
		this.id = id;
		this.title = title;
		this.placeId = placeId;
		this.placeTitle = placeTitle;
		this.placeContent = placeContent;
		this.placeFileId = placeFileId;
		this.updatedAt = updatedAt;
		this.memberId = memberId;
		this.memberNickname = memberNickname;
		this.badgeId = badgeId;
		this.viewCount = viewCount;
		if (filename != null)
			this.thumbnailFilename = "t_" + filename + ".jpg";
		this.likeCount = likeCount;

		if (memberProfileFilename != null)
			this.memberThumbail = "t_" + memberProfileFilename + ".jpg";
	}
}
