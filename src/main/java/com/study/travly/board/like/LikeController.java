package com.study.travly.board.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("board")
public class LikeController {
	
	@Autowired
	LikeService likeService;
	
}
