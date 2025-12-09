package com.study.travly.board;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;

import com.study.travly.board.place.BoardPlace;
import com.study.travly.member.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_board__member_member_id"))
	private Member member;

	@Comment("조회수")
	private int viewCount;

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

	@BatchSize(size = 10) // 갯수 제한
	@OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
	@OrderBy("orderNum ASC")
	private Set<BoardPlace> places;
}
