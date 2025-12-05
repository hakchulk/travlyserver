package com.study.travly.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("member")
public class MemberController {
	@Autowired
	MemberService memberService;

	@PostMapping
	public Member modifyMemeber(@RequestBody MemberModifyRequest req) {
		return memberService.modifyMember(req);
	}

}
