package com.study.travly.qna;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import com.study.travly.file.File;
import com.study.travly.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "qna_board")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class QnaBoard {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_qna_board__member_id"))
	private Member member;

	@Comment("첨부 파일")
	@ManyToOne
	@JoinColumn(name = "file_id", nullable = false, unique = false, foreignKey = @ForeignKey(name = "fk_qna_board__file_id"))
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
