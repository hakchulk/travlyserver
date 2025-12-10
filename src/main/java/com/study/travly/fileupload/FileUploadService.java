package com.study.travly.fileupload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.study.travly.exception.BadRequestException;
import com.study.travly.file.File;
import com.study.travly.file.FileRepository;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Service
@Slf4j
public class FileUploadService {
	@Value("${file.upload-dir}")
	String fileDir;

	@Autowired
	FileRepository fileRepo;

	public List<File> fileCreate(FileUploadDto dto) throws IOException {
		return fileCreate(dto, 200, 200);
	}

	public List<File> fileCreate(FileUploadDto dto, int thumbSize) throws IOException {
		return fileCreate(dto, thumbSize, thumbSize);
	}

	public static String getFileExtension(String filename) {
		// 1. 마지막 점(.)의 인덱스를 찾습니다.
		int lastDotIndex = filename.lastIndexOf('.');

		// 2. 점이 파일명에 없거나, 점이 맨 앞에만 있는 경우 (숨김 파일 등)
		if (lastDotIndex == -1 || lastDotIndex == 0) {
			return ""; // 확장자가 없거나 유효하지 않음
		}

		// 3. 점을 포한한 문자열(확장자)을 반환합니다.
		return filename.substring(lastDotIndex);
	}

	// ret : new filename list
	public List<File> fileCreate(FileUploadDto dto, int thumbX, int thumbY) throws IOException {

		List<MultipartFile> files = dto.getFiles();

		if (files == null || files.isEmpty())
			throw new BadRequestException(String.format("key files가 존재하지 않거나 비어있습니다."));

		log.info("----fileCreate() files.size() : " + files.size());

		List<com.study.travly.file.File> lst = new ArrayList<>();

		for (MultipartFile file : files) {
			if (file != null && !file.isEmpty()) {
				String originalFilename = file.getOriginalFilename();
				String ext = getFileExtension(originalFilename);

				// originalFilename를 뒤에 붙이면 파일시스템에 따른 엔코딩 문제가 생길 수 있어서 ext만 붙인다.
				String newName = UUID.randomUUID() + ext;
				log.info("----fileUpload() getOriginalFilename() : " + originalFilename);
				java.io.File folder = new java.io.File(fileDir);
				if (!folder.exists())
					folder.mkdirs(); // 하위 폴더 모두 생성

				byte[] fileData = file.getBytes();

				Files.write(Paths.get(fileDir + '/' + newName), fileData); // 원본 저장

				if (isImage(originalFilename)) {
                    // ⭐ 1. 새 파일명에서 기존 확장자 제거
                    String thumbnailBaseName = newName;
                    int lastDot = newName.lastIndexOf('.');
                    if (lastDot > 0) {
                        thumbnailBaseName = newName.substring(0, lastDot);
                    }
                    
                    // ⭐ 2. 썸네일 저장 시 확장자가 제거된 BaseName 사용
					Thumbnails.of(new ByteArrayInputStream(fileData))
                        .size(thumbX, thumbY)
                        .outputFormat("jpg") // Thumbnails 라이브러리가 여기에 .jpg를 추가할 것입니다.
                        .toFile(fileDir + "/t_" + thumbnailBaseName); // **.png가 제거된 경로**

                    // ⭐ 3. Service에서 사용할 썸네일 파일명 정의
                    // 디스크에 저장되는 실제 썸네일 파일명은 "t_" + thumbnailBaseName + ".jpg" 입니다.
				}

				File fileEntity = File.builder().filename(newName).org_filename(originalFilename).build();
				File newFileEntity = fileRepo.save(fileEntity);

				lst.add(newFileEntity);
			}

		}

		return lst;
	}

	public File findById(Long fileId) {
		File file = fileRepo.findById(fileId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 file.id [%d]", fileId)));
		return file;
	}

	public static boolean isImage(String fileName) {
		String name = fileName.toLowerCase();
		return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif")
				|| name.endsWith(".bmp") || name.endsWith(".webp");
	}
}
