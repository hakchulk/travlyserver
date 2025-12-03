package com.study.travly.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.study.travly.filter.category.Category;

@RequestMapping("filter")
@Controller
public class FilterController {

	@Autowired
	private FilterService filterService;

	@PostMapping
	public ResponseEntity<Category> createFilter(@RequestBody FilterRequest request) {

		// 서비스 호출: Category와 Item 목록을 동시에 처리
		Category savedCategory = filterService.createFilterCategoryAndItems(request);

		// 저장된 Category 객체를 응답으로 반환
		return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
	}

	@GetMapping
	@ResponseBody
	public List<Category> getAllCategoryItems() {

		// 서비스 호출: Category와 Item 목록을 동시에 처리
		List<Category> lst = filterService.getAllCategoryItems();

		// 저장된 Category 객체를 응답으로 반환
		return lst;
	}
}
