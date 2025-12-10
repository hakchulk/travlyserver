package com.study.travly.board;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentBoardDTO {

	private Long id;
	
	private String title;
	private String createdAt;
	private Long likeCount;
	private Long viewCount;
	
	private long memberId;
	private String memberName;
	private Long badgeId;
	private String profileImg;
	
	private String cardImg;
	private List<String> tags;
	private String content;
}
