package com.study.travly.member;

import java.util.UUID;

//AuthUserProjection.java (인터페이스 기반 Projection)
public interface AuthUserProjection {
	// Supabase auth.users의 컬럼 이름과 메서드 이름이 일치해야 합니다.
	UUID getId();

	String getEmail();
	// ... 필요한 필드 추가
}