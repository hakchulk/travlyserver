package com.study.travly.fileupload;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.travly.file.File;

@RestController
public class FileUploadController {
	@Autowired
	FileUploadService fileService;

	@Value("${file.upload-dir}")
	String fileDir;

	@PostMapping("fileupload")
	public List<File> fileUpload(@ModelAttribute FileUploadDto req) throws IOException {

		List<File> lst = fileService.fileCreate(req);
		return lst;
	}

}
