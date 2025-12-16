package com.study.travly.auth;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.study.travly.member.MemberRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	@Autowired
	private MemberRepository memberReporitory;

	public JwtAuthFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				var claims = jwtUtil.validateToken(token);
				Long memberId = jwtUtil.extractMemberId(claims);

				// 개발 초기에는 memberId이 null 일 수 있다.
				if (memberId == null) {
					UUID uuid = UUID.fromString(claims.getSubject());
					memberId = memberReporitory.getIdByAuthUuid(uuid);
				}
				CustomUserPrincipal principal = new CustomUserPrincipal(claims.getSubject(), memberId);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null,
						principal.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (Exception e) {
				// 토큰 검증 실패 시 처리
			}
		}
		filterChain.doFilter(request, response);
	}
}
