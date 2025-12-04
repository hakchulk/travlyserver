package com.study.travly.fileupload;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.travly.file.File;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody; // 명확한 RequestBody 정의를 위해 사용
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "파일 업로드 API", description = "파일 업로드")
@RestController
public class FileUploadController {
	@Autowired
	FileUploadService fileService;

	@Value("${file.upload-dir}")
	String fileDir;

	@Operation(summary = "다중 파일 업로드", description = "하나 이상의 파일을 서버에 업로드하고, 저장된 파일 정보를 반환합니다. 폼 데이터(multipart/form-data)를 사용함."
			+ "<br>파일저장폴더:application.properties의 file.upload-dir (초기값:c:/travly/upload)",
			// 1. 파일 업로드는 폼 데이터(multipart/form-data)를 사용함을 명시합니다.
			requestBody = @RequestBody(content = @Content(
					// mediaType을 multipart/form-data로 설정
					mediaType = "multipart/form-data",
					// schema를 FileUploadDto로 지정하여 어떤 필드를 받는지 명시
					schema = @Schema(implementation = FileUploadDto.class),
					// 파일 리스트 필드에 대한 인코딩 정보를 추가 (선택적)
					encoding = @Encoding(name = "files", contentType = "application/octet-stream"))))
	@ApiResponse(responseCode = "200", description = "파일 업로드 및 저장 성공, 저장된 파일 정보 리스트 반환",
			// 2. 응답 모델이 List<File> 임을 명시
			content = @Content(
					// 배열 타입 응답 명시
					schema = @Schema(implementation = File.class, type = "array")))

	@PostMapping("fileupload")
	public List<File> fileUpload(@ModelAttribute FileUploadDto req) throws IOException {

		List<File> lst = fileService.fileCreate(req);
		return lst;
	}

}
