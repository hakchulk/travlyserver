package com.study.travly.member;

import java.time.LocalDateTime;

import com.study.travly.badge.Badge;
import com.study.travly.file.File;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member", uniqueConstraints = {
		@UniqueConstraint(name = "UK_member_auth_uuid", columnNames = { "auth_uuid" } // DB 컬럼 이름
		) })
@Getter
@Setter
@NoArgsConstructor // default Constructor
@AllArgsConstructor
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "auth_uuid", nullable = false, foreignKey = @ForeignKey(name = "fk_member__auth_uuid"))
	private AuthUser authUser;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private int gender; // 1 : man, 2 : woman

	private String introduction;
	private LocalDateTime birthday;

	@ManyToOne
	@JoinColumn(name = "badge_id", nullable = false, foreignKey = @ForeignKey(name = "fk_member__badge_badge_id"))
	private Badge badge;

	// @OneToOne이면 디폴트로 사용 할 file_id를 공유 수 없다.
	@ManyToOne
	@JoinColumn(name = "file_id", nullable = false, unique = false, foreignKey = @ForeignKey(name = "fk_member__file_id"))
	private File file;

	@Column(nullable = false)
	private LocalDateTime createdAt;
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreated() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = this.createdAt;
	}

	@PreUpdate
	public void onUpdatedd() {
		this.updatedAt = LocalDateTime.now();
	}
}
