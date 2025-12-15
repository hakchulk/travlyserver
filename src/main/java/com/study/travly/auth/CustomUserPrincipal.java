package com.study.travly.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserPrincipal implements UserDetails {
	private static final long serialVersionUID = 1L; // ✅ 추가

	private final String userId;
	private final Long memberId;

	public CustomUserPrincipal(String userId, Long memberId) {
		this.userId = userId;
		this.memberId = memberId;
	}

	public Long getMemberId() {
		return memberId;
	}

	@Override
	public String getUsername() {
		return userId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return java.util.List.of();
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
