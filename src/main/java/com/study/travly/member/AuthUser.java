package com.study.travly.member;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users", schema = "auth")
public class AuthUser {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "email")
	private String email;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	// 필요한 필드만 추가 (Supabase가 관리하므로 최소한만 매핑)
	// getter/setter ...
}
