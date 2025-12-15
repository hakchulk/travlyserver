package com.study.travly.board.like;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.travly.member.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("board")
@AllArgsConstructor
public class LikeController {
	
	@Autowired
	LikeService likeService;
	@Autowired
	MemberService memberService;
	
	@Operation(
	        summary = "게시물 좋아요/취소 토글",
	        description = "인증된 사용자가 특정 게시물에 좋아요를 등록하거나 이미 등록된 좋아요를 취소합니다."
	    )
	@PostMapping("/{boardId}/like")
    public ResponseEntity<String> toggleLike(
            HttpServletRequest request, 
            @PathVariable("boardId") Long boardId) {
      
        UUID authUuid = (UUID) request.getAttribute("authUuid");
        log.info("요청 UUID 확인: {}", authUuid); 

        if (authUuid == null) {
            log.warn("🚨 비인증 사용자 접근 시도: {}", request.getRequestURI());
            return new ResponseEntity<>("인증 정보가 없습니다. 로그인이 필요합니다.", HttpStatus.UNAUTHORIZED); 
        }

        try {
            Long memberId = memberService.getLoggedInMember(authUuid).getId(); 
            log.info("인증된 사용자 ID: {}", memberId);
            
            // 좋아요 토글 로직 실행 (Service 계층 위임)
            boolean isLiked = likeService.toggleLike(memberId, boardId);

            // 결과에 따른 응답 메시지 생성
            String message = isLiked ? "좋아요가 등록되었습니다." : "좋아요가 취소되었습니다.";
            
            // 200 OK 상태 코드와 함께 메시지 반환
            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            // MemberService 또는 LikeService에서 예외(예: ResourceNotFoundException)가 발생한 경우 처리
            log.error("좋아요 처리 중 오류 발생: {}", e.getMessage());
            // 400 Bad Request 또는 500 Internal Server Error 등으로 처리할 수 있습니다.
            return new ResponseEntity<>("좋아요 처리 중 문제가 발생했습니다: " + e.getMessage(), HttpStatus.BAD_REQUEST); 
        }
    }
	
}