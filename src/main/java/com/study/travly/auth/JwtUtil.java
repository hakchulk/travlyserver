package com.study.travly.auth;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
	private final SecretKey secretKey;

	public JwtUtil(String jwtSecret) {
		this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public Claims validateToken(String token) {
		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
	}

	public Long extractMemberId(Claims claims) {
		Object metadataObj = claims.get("user_metadata");
		if (metadataObj instanceof Map<?, ?> metadata) {
			Object memberIdObj = metadata.get("memberId");
			if (memberIdObj instanceof Number number) {
				return number.longValue();
			}
			if (memberIdObj instanceof String str) {
				try {
					return Long.parseLong(str);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		}
		return null;
	}
}
