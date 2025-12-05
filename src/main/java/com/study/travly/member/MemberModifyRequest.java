package com.study.travly.member;

import java.util.UUID;

import lombok.Data;

@Data
public class MemberModifyRequest {
	private UUID authUuid;
	private String name;
	private String nickname;
	private String introduction;
	private Long profileImageFileId;
}
