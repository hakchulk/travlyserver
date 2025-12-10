package com.study.travly.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {
	@Autowired
	FileRepository fileRepo;

	public String getFilenameById(Long fileId) {
	    // fileId를 사용하여 DB의 file 테이블에서 난수명(filename)을 조회하는 로직 구현
	    File file = fileRepo.findById(fileId).orElse(null);
	    return file != null ? file.getFilename() : null;
	}
}
