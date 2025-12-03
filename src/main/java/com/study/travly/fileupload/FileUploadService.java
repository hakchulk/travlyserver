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
		return fileCreate(dto, 100, 100);
	}

	public List<File> fileCreate(FileUploadDto dto, int thumbSize) throws IOException {
		return fileCreate(dto, thumbSize, thumbSize);
	}

	// ret : new filename list
	public List<File> fileCreate(FileUploadDto dto, int thumbX, int thumbY) throws IOException {

		List<MultipartFile> files = dto.getFiles();
		if (files != null && !files.isEmpty())
			log.info("----fileCreate() files.size() : " + files.size());

		List<com.study.travly.file.File> lst = new ArrayList<>();

		for (MultipartFile file : files) {
			if (file != null && !file.isEmpty()) {
				String originalFilename = file.getOriginalFilename();
				String newName = UUID.randomUUID() + "_" + originalFilename;
				log.info("----fileUpload() getOriginalFilename() : " + originalFilename);
				java.io.File folder = new java.io.File(fileDir);
				if (!folder.exists())
					folder.mkdir();

				byte[] fileData = file.getBytes();

				Files.write(Paths.get(fileDir + '/' + newName), fileData); // 원본 저장

				if (isImage(originalFilename))
					Thumbnails.of(new ByteArrayInputStream(fileData)).size(thumbX, thumbY).outputFormat("jpg")
							.toFile(fileDir + "/t_" + newName); // thumbnail 저장

				File fileEntity = File.builder().filename(newName).org_filename(originalFilename).build();
				File newFileEntity = fileRepo.save(fileEntity);

				lst.add(newFileEntity);
			}

		}

		return lst;
	}

	public static boolean isImage(String fileName) {
		String name = fileName.toLowerCase();
		return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif")
				|| name.endsWith(".bmp") || name.endsWith(".webp");
	}
}
