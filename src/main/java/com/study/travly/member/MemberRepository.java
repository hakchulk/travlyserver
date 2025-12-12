package com.study.travly.member;

import java.util.Optional;
import java.util.UUID;

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

	@Modifying // DML (Update, Delete) 쿼리임을 명시
	@Query(value = "UPDATE auth.users "
			+ "SET raw_user_meta_data = raw_user_meta_data || jsonb_build_object('memberId', :memberId) "
			+ "WHERE id = :uuid", nativeQuery = true // Native SQL 사용을 명시
	)
	int updateRawUserMetaData(@Param("uuid") UUID uuid, @Param("memberId") Long memberId);

	// board comment 가 create 될 때 comment.board.member.notificationCount을 1 증가 시킨다. 
	@Modifying
	@Query("UPDATE Member m SET m.notificationCount = m.notificationCount + 1 WHERE m.id = :id")
	int incrementNotificationCount(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Member m SET m.notificationCount = 0 WHERE m.id = :id")
	int initNotificationCount(@Param("id") Long id);

	// AuthUser의 id(UUID)로 Member 검색
	// findByAuthUser_Id → JPA가 Member 엔티티의 authUser 필드 안에 있는 id를 자동으로 탐색합니다.
	Optional<Member> findByAuthUser_Id(UUID id);

	boolean existsByNickname(String nickname);

	@Query(value = "select id, email from auth.users u where u.id =:user_uuid", nativeQuery = true)
	Optional<AuthUserProjection> getAuthUserProcedure(@Param("user_uuid") UUID userUuid);

	@Query(value = "select id, email from auth.users u where u.email =:user_email", nativeQuery = true)
	Optional<AuthUserProjection> getAuthUserProcedure(@Param("user_email") String user_email);

	@Query(value = "SELECT CASE WHEN COUNT(u.id) > 0 THEN TRUE ELSE FALSE END "
			+ "FROM auth.users u WHERE u.email = :user_email", nativeQuery = true)
	boolean isEmailExist(@Param("user_email") String user_email);

	public Optional<Member> findById(Long id);

	@Query(value = """
			SELECT email
			FROM public.member m join auth.users u on m.auth_uuid = u.id WHERE m.id = :memberId
			""", nativeQuery = true)
	String getEmailById(@Param("memberId") Long memberId);

}