package com.study.travly.member;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", schema = "auth")
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {

	@Id
	@Column(nullable = false)
	private UUID id;

	//	private String email;
	//	private LocalDateTime createdAt;
}
