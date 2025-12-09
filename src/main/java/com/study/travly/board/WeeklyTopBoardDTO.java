package com.study.travly.board;

import java.util.List;

import com.study.travly.board.like.Like;
import com.study.travly.filter.item.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyTopBoardDTO {

	private Long id;

	private String title;
	private String createdAt;
	private Long likeCount;
	private Long viewCount;
	
	private Long memberId;
	private String memberName;
	private Long badgeId;
	private String profileImg;
	
	private List<String> tags;
	private String content;
}
