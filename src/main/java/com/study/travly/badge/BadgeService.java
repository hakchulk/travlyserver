package com.study.travly.badge;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정
public class BadgeService {

	@Autowired
	private BadgeRepository badgeRepository;

	/**
	 * 모든 Badge 목록을 조회합니다.
	 * @return List<Badge>
	 */
	public List<Badge> findAllBadges() {
		// JpaRepository의 기본 메서드인 findAll()을 호출합니다.
		return badgeRepository.findAll();
	}

}