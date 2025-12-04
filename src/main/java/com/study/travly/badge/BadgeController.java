package com.study.travly.badge;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "뱃지 조회 API", description = "시스템에 존재하는 모든 뱃지 정보를 제공합니다.")
@RestController
public class BadgeController {

	@Autowired
	private BadgeService badgeService;

	@Operation(summary = "모든 뱃지 목록 조회", description = "")
	@GetMapping("badge")
	public List<Badge> getAllBadges() {
		// Service를 통해 모든 뱃지 데이터를 조회합니다.
		return badgeService.findAllBadges();
	}
}