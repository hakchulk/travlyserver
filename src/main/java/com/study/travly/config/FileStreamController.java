package com.study.travly.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/file")
public class FileStreamController {
	
	@Value("${file.upload-dir}")
	private String fileDir;
	
	@GetMapping("/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("filename") String filename) {
        try {
            // 1. 파일 경로 생성 (OS 독립적)
            Path filePath = Paths.get(fileDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // 2. 파일이 존재하는지 확인
            if (resource.exists() && resource.isReadable()) {
                
                // 3. 파일 타입을 추정 (썸네일은 .jpg이므로 image/jpeg로 가정)
                String contentType = "image/jpeg"; 
                if (filename.toLowerCase().endsWith(".png")) {
                    contentType = "image/png";
                }
                
                // 4. HTTP 응답 구성 (파일을 직접 스트리밍)
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"") // 브라우저가 직접 렌더링하도록 설정
                        .body(resource);
            } else {
                // 파일이 디스크에 없거나 읽을 수 없을 때 404 반환
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            // URL 형식 오류 발생 시 400 Bad Request 반환
            return ResponseEntity.badRequest().build();
        }
    }
}
