package com.study.travly.board.place.file;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.study.travly.board.place.BoardPlace;
import com.study.travly.file.File;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "board_place_file", uniqueConstraints = {
		@UniqueConstraint(name = "UK_board_place_file__board_place_id_file_id", columnNames = { "board_place_id",
				"file_id" }) })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "orderNum", "id" }) // BoardPlace.files = Set<>이므로 hashCode()와 equals() 구현 필요
public class BoardPlaceFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_place_id", nullable = false, foreignKey = @ForeignKey(name = "fk_board_place_file__board_place_id"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private BoardPlace boardPlace;

	// @OneToOne() 을 사용하면 file_id field에 unique index를 생성 해서 file_id을 중복 사용 할 수 없다.
	// product 에서는 @OneToOne()을 사용 하는 것이 바람직
	@ManyToOne
	@JoinColumn(name = "file_id", nullable = false, unique = false, foreignKey = @ForeignKey(name = "fk_board_place_file__file_id"))
	private File file;

	@Column(nullable = false)
	private int orderNum; // db keyword 'order' 와 충돌 회피

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreated() {
		this.createdAt = LocalDateTime.now();
	}
}
