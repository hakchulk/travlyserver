package com.study.travly.board.filter;

import lombok.Getter;

@Getter
public class BoardFilterItemResponseDto {
	private Long id; // BoardFilterItem의 ID
	private Long itemId; // Item의 ID
	private String itemName; // Item의 이름 (Item 엔티티에 getName()이 있다고 가정)

	// 엔티티를 받아 DTO 필드를 초기화하는 생성자
	public BoardFilterItemResponseDto(BoardFilterItem entity) {
		this.id = entity.getId();
		// Item 필드에 접근하여 필요한 정보만 추출
		this.itemId = entity.getFilterItem().getId();
		// Item 엔티티가 'name' 필드를 가지고 있다고 가정
		this.itemName = entity.getFilterItem().getName();
	}
}