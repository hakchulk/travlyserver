package com.study.travly.board.bookmark;

import java.time.LocalDateTime;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bookmark", uniqueConstraints = {
		@UniqueConstraint(name = "UK_bookmark__member_board", columnNames = { "member_id", "board_id" }) })
@Getter
@Setter
@AllArgsConstructor
public class Bookmark {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne()
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bookmark__member_id"))
	private Member member;

	@ManyToOne()
	@JoinColumn(name = "board_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bookmark__board_id"))
	private Board board;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreated() {
		this.createdAt = LocalDateTime.now();
	}
}
