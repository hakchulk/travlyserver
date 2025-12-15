package com.study.travly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.study.travly.auth.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // 메서드 단위 보안 활성화
public class SecurityConfig {
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
		return http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.formLogin(form -> form.disable()).httpBasic(httpBasic -> httpBasic.disable())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();

		// 				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
		// auth -> auth.requestMatchers("/public/**").permitAll().anyRequest().authenticated())					
		// .formLogin(form -> form.permitAll()) // /login 페이지 허용

	}

	//	@Bean
	//	UserDetailsService userDetailsService() {
	//		UserDetails user = User.withUsername("myuser").password("{noop}mypassword") // {noop}은 암호화 없이 사용
	//				.roles("USER").build();
	//		return new InMemoryUserDetailsManager(user);
	//	}

}
