package com.study.travly.member;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class AuthFilter implements Filter {
    
    // ⭐ 클라이언트에서 UUID를 보낼 헤더의 이름입니다.
    private static final String AUTH_HEADER_KEY = "X-AUTH-UUID";
    
    // ⭐ Controller에서 UUID를 가져올 때 사용할 request 속성 이름입니다.
    private static final String AUTH_ATTRIBUTE_KEY = "authUuid"; 

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 1. HTTP 요청 헤더에서 UUID 값(문자열)을 추출합니다.
        String uuidStr = httpRequest.getHeader(AUTH_HEADER_KEY);

        if (uuidStr != null && !uuidStr.isEmpty()) {
            try {
                // 2. 추출한 문자열을 UUID 객체로 변환합니다.
                UUID authUuid = UUID.fromString(uuidStr);
                
                // 3. Controller가 사용할 수 있도록 request 객체의 속성에 저장합니다.
                httpRequest.setAttribute(AUTH_ATTRIBUTE_KEY, authUuid); 
                
            } catch (IllegalArgumentException e) {
                // UUID 형식이 잘못되었을 경우 400 Bad Request 응답 (선택 사항)
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID format in header");
                return;
            }
        } else {
            // 4. UUID가 없거나 비어있는 경우, null 상태로 다음 체인으로 넘깁니다. 
            //    Controller에서 authUuid가 null인지 체크하게 됩니다.
        }
        
        // 5. 다음 필터 또는 DispatcherServlet(Controller)으로 요청을 넘깁니다.
        chain.doFilter(request, response);
    }
}