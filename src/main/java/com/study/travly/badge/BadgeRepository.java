package com.study.travly.badge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
	// JpaRepository를 상속받으면 모든 CRUD 메서드가 자동으로 제공됩니다.

}