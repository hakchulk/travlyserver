package com.study.travly.fileupload;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class FileUploadDto {
	public List<MultipartFile> files;
}
