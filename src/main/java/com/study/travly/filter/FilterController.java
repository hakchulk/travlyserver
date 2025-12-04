package com.study.travly.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.study.travly.filter.category.Category;

import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("filter")
@Controller
public class FilterController {

	@Autowired
	private FilterService filterService;

	//	@PostMapping
	//	public ResponseEntity<Category> createFilter(@RequestBody FilterRequest request) {
	//
	//		// 서비스 호출: Category와 Item 목록을 동시에 처리
	//		Category savedCategory = filterService.createFilterCategoryAndItems(request);
	//
	//		// 저장된 Category 객체를 응답으로 반환
	//		return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
	//	}

	@Operation(summary = "모든 filter 정보 얻기", description = "조회 기능에서 filter 선택할 떄 사용")
	@GetMapping
	@ResponseBody
	public List<Category> getAllCategoryItems() {

		// 서비스 호출: Category와 Item 목록을 동시에 처리
		List<Category> lst = filterService.getAllCategoryItems();

		// 저장된 Category 객체를 응답으로 반환
		return lst;
	}
}
