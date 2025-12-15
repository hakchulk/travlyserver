package com.study.travly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@OpenAPIDefinition(info = @Info(title = "travly 프로젝트 API", description = "사용자의 여행 경험을 저장하고 공유합니다.", version = "v0.1"))
@Configuration
public class OpenApiConfig {
	// 추가적인 Security Scheme 설정 등을 여기에 할 수 있습니다.
	
	// 사용할 보안 스키마의 이름입니다.
    private static final String SECURITY_SCHEME_NAME = "AuthUUIDHeader";
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // 1. 컴포넌트에 보안 스키마를 정의합니다.
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER) // ⬅️ 인증 정보가 헤더에 있음을 명시
                                .name("X-AUTH-UUID") // ⬅️ 사용하실 헤더 이름 명시
                                .description("Supabase 인증 후 받은 사용자 UUID 입력")
                        )
                )
                // 2. 정의된 스키마를 API에 적용하도록 명시합니다.
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}