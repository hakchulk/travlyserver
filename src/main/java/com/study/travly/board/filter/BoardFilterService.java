package com.study.travly.board.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.travly.board.Board;
import com.study.travly.board.BoardRepository;
import com.study.travly.filter.item.Item;
import com.study.travly.filter.item.ItemRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BoardFilterService {
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private BoardFilterItemRepository boardFilterItemRepository;
	@Autowired
	private ItemRepository itemRepository;

	public void saveBoardFilterItems(Long boarId, List<Long> filterItemIds) {
		saveBoardFilterItems(new BoardFilterItemsSaveRequest(boarId, filterItemIds));
	}

	/**
	 * BoardItemsSaveRequest를 사용하여 게시글과 필터 아이템 관계를 저장/갱신합니다.
	 * 
	 * @param request Board ID와 연결할 Item ID 목록
	 */
	@Transactional
	public void saveBoardFilterItems(BoardFilterItemsSaveRequest request) {
		Long boardId = request.getBoardId();
		// 1. 기존 Board 엔티티 로드
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new EntityNotFoundException("Board not found with ID: " + request.getBoardId()));

		// 2. 기존 BoardFilterItem 관계 모두 삭제 (갱신을 위해)
		boardFilterItemRepository.deleteByBoardId(board.getId());

		// 요청된 item_ids가 비어있으면 삭제만 하고 종료
		if (request.getItemIds() == null || request.getItemIds().isEmpty()) {
			return;
		}

		// 3. 요청된 Item ID 목록으로 Item 엔티티를 모두 조회
		// Item ID를 사용하는 이유는 filter_item 테이블의 기본 키이기 때문입니다.
		List<Item> items = itemRepository.findAllById(request.getItemIds());

		// 4. 새로운 BoardFilterItem 연결 관계 생성
		// orderNum은 현재 요청 DTO에 없으므로, 간단하게 스트림 인덱스를 사용하거나 0으로 초기화합니다.
		List<BoardFilterItem> newBoardFilterItems = new ArrayList<>();

		// for 루프를 사용하여 순차적으로 orderNum을 부여
		for (Item item : items) {
			BoardFilterItem boardFilterItem = new BoardFilterItem(null, // id (DB 자동 생성)
					board, item, // filterItem
					null // createdAt (@PrePersist가 채움)
			);
			newBoardFilterItems.add(boardFilterItem);
		}

		// 5. 새로운 관계 저장
		boardFilterItemRepository.saveAll(newBoardFilterItems);
	}

	@Transactional(readOnly = true)
	public List<BoardFilterItemResponseDto> findByBoardId(Long boardId) {

		// 1. Repository를 통해 엔티티 목록 조회
		List<BoardFilterItem> items = boardFilterItemRepository.findAllByBoardIdOrderByFilterItemId(boardId);

		// 2. 조회된 엔티티 목록을 DTO로 변환 (트랜잭션 내에서 실행)
		return items.stream().map(BoardFilterItemResponseDto::new).collect(Collectors.toList());
	}

}
