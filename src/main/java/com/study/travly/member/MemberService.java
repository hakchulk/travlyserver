package com.study.travly.member;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.travly.badge.Badge;
import com.study.travly.badge.BadgeRepository;
import com.study.travly.exception.BadRequestException;
import com.study.travly.file.File;
import com.study.travly.file.FileRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemberService {
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private BadgeRepository badgeRepository;
	@Autowired
	private AuthUserRepository authUserRepository;
	
	
	private Member getNewMember(MemberModifyRequest request) {
		AuthUser authUser = new AuthUser(request.getAuthUuid());
	

		// 기본 뱃지 (가장 낮은 등급 등) 조회. (ID 1L을 기본 뱃지로 가정)
		Badge defaultBadge = badgeRepository.findById(1L)
				.orElseThrow(() -> new IllegalStateException("기본 뱃지(ID: 1L) 설정을 찾을 수 없습니다."));

		// DTO의 필드를 사용하여 새 Member 엔티티 생성
		Member member = Member.builder() // @Builder가 Member 엔티티에 추가되었다고 가정
				.authUser(authUser).nickname(request.getNickname())
				.introduction(request.getIntroduction() != null ? request.getIntroduction() : "").badge(defaultBadge)
				.notificationCount(0) // 기본값 설정
				.build();

		return member;
	}

	private Member getUpdatedMember(Member member, MemberModifyRequest request) {
		// DTO의 정보로 엔티티 업데이트

		// 닉네임 수정 (중복 검사 로직 필요)
		if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
			// **중복 검사:** 닉네임이 변경되었고, 해당 닉네임이 이미 DB에 존재하며, 그 닉네임이 현재 회원의 것이 아닌지 확인
			if (!member.getNickname().equals(request.getNickname())
					&& memberRepository.existsByNickname(request.getNickname())) {
				throw new BadRequestException("이미 사용 중인 닉네임: " + request.getNickname());
			}
			member.setNickname(request.getNickname());
		}

		// 2-3. 소개 수정
		if (request.getIntroduction() != null) {
			member.setIntroduction(request.getIntroduction());
		}

		// 2-4. 프로필 이미지 파일 수정
		Long fileId = request.getProfileImageFileId();
		if (fileId != null) {
			if (fileId == -1L) {
				// -1L 등의 특정 값은 프로필 이미지 삭제 요청으로 가정할 수 있습니다.
				member.setProfileImage(null);
			} else {
				// 유효한 파일 ID로 File 엔티티 조회
				File profileImage = fileRepository.findById(fileId)
						.orElseThrow(() -> new BadRequestException("유효하지 않은 파일 ID입니다: " + fileId));
				member.setProfileImage(profileImage);
			}
		}

		return member;
	}

	@Transactional
	public Member modifyCreateMember(MemberModifyRequest request) {
		AuthUserProjection userProj = memberRepository.getAuthUserProcedure(request.getAuthUuid())
				// 2. Optional 처리: 결과가 없으면 예외 발생
				.orElseThrow(() -> new BadRequestException("등록되지 않은 인증 사용자 uuid입니다: " + request.getAuthUuid()));

		log.info("============== MemberService.modifyCreateMember()" + userProj.getEmail());
		// 1. AuthUuid를 기반으로 Member 엔티티 조회 시도
		Member member = memberRepository.findByAuthUser_Id(request.getAuthUuid()).orElse(null);

		// 2. 엔티티가 존재하지 않으면, 새로 생성 (Create)
		if (member == null) {
			// 새 엔티티이므로 save()를 호출하여 영속화(INSERT)
			member = memberRepository.save(getNewMember(request));
		} else {
			member = getUpdatedMember(member, request);
		}

		String email = memberRepository.getEmailById(member.getId());
		member.setEmail(email);

		return member;
	}

	public boolean checkExistence(String email, String nickname) {

		boolean isExist = true;

		if (email != null) {
			// Service 레이어에서 해당 이메일이 이미 존재하는지 확인
			isExist = memberRepository.isEmailExist(email);
		}

		if (nickname != null) {
			// Service 레이어에서 해당 닉네임이 이미 존재하는지 확인
			isExist = memberRepository.existsByNickname(nickname);
		}
		return isExist;
	}

	public Member getMember(Long id) {
		Member m = memberRepository.findById(id)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 member.id [%d]", id)));
		String email = memberRepository.getEmailById(id);
		m.setEmail(email);
		return m;
	}
	
	@Transactional(readOnly = true)
	public Member getLoggedInMember(UUID authUuid) {
		// UUID와 멤버 아이디 조회
		Member member = memberRepository.findByAuthUser_Id(authUuid)
		        .orElseThrow(() -> new BadRequestException("UUID에 해당하는 Member를 찾을 수 없습니다: " + authUuid));
		    return member;
	}
	
	
}
