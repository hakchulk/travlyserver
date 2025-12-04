package com.study.travly.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.travly.filter.category.Category;
import com.study.travly.filter.category.CategoryRepository;
import com.study.travly.filter.item.Item;
import com.study.travly.filter.item.ItemRepository;

//Item 생성을 위한 DTO
record ItemRequest(String name) {
}

//Category와 Item 목록을 담는 메인 요청 DTO
record FilterRequest(String name, int multiSelectCount, int orderNum,
		// Category에 종속될 Item 목록
		Set<ItemRequest> items) {
}

@Service
public class FilterService {
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ItemRepository itemRepository;

	@Transactional
	public Category createFilterCategoryAndItems(FilterRequest request) {

		// 1. Category 엔티티 생성 및 저장
		Category category = new Category(null, // id는 @GeneratedValue로 자동 생성
				request.name(), request.multiSelectCount(), request.orderNum(), null // createdAt은 @PrePersist로 자동 생성
				, null);
		Category savedCategory = categoryRepository.save(category);

		int i = 0;
		// 2. Item 목록 생성 및 저장
		Set<Item> itemsToSave = new HashSet<Item>();
		for (ItemRequest itemRequest : request.items()) {
			Item item = new Item(null, // id는 @GeneratedValue로 자동 생성
					itemRequest.name(), i++, null, // createdAt은 @PrePersist로 자동 생성
					savedCategory // 🌟 외래 키 관계 설정

			);
			itemsToSave.add(item);
		}

		// Item 목록을 DB에 한 번에 저장 (Optional: 성능을 위해 batch insert 사용 가능)
		itemRepository.saveAll(itemsToSave);

		// 3. (Optional) 연관 관계를 메모리상의 savedCategory에 다시 설정하여 반환
		// 영속성 컨텍스트 덕분에 itemsToSave가 저장되었지만, 관계를 명시적으로 설정해줍니다.
		// 이 예시에서는 Category 엔티티에 Item 리스트 필드가 없으므로 생략합니다.

		return savedCategory;
	}

	public List<Category> getAllCategoryItems() {
		return categoryRepository.getAllCategoryItems();
	}
}
