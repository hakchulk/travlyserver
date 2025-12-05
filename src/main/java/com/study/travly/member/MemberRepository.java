package com.study.travly.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Member 엔티티를 위한 Repository 인터페이스입니다. Spring Data JPA의 JpaRepository를 상속받아 기본적인
 * CRUD 기능을 제공받습니다.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	// board comment 가 create 될 때 comment.board.member.notificationCount을 1 증가 시킨다. 
	@Modifying
	@Query("UPDATE Member m SET m.notificationCount = m.notificationCount + 1 WHERE m.id = :id")
	int incrementNotificationCount(@Param("id") Long id);
}