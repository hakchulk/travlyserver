package com.study.travly.board.comment;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.study.travly.board.Board;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comment", uniqueConstraints = {
		@UniqueConstraint(name = "UK_comment__board_member", columnNames = { "board_id", "member_id" }) })
@Getter
@Setter
@NoArgsConstructor // default Constructor
@AllArgsConstructor

public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne()
	@JoinColumn(name = "board_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_board_id"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Board board;

	@ManyToOne()
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_member_id"))
	private Member member;

	private String comment;

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
