package com.study.travly.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "travly 프로젝트 API", description = "사용자의 여행 경험을 저장하고 공유합니다.", version = "v0.1"))
@Configuration
public class OpenApiConfig {
	// 추가적인 Security Scheme 설정 등을 여기에 할 수 있습니다.
}