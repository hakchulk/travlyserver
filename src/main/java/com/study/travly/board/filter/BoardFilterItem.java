package com.study.travly.board.filter;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.study.travly.board.Board;
import com.study.travly.filter.item.Item;

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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "board_filter_item", uniqueConstraints = {
		@UniqueConstraint(name = "UK_board_filter_item__board_filter_item", columnNames = { "board_id",
				"filter_item_id" }) })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardFilterItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne()
	@JoinColumn(name = "board_id", nullable = false, foreignKey = @ForeignKey(name = "fk_board_filter_item__board_id"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Board board;

	@ManyToOne()
	@JoinColumn(name = "filter_item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_board_filter_item__filter_item_id"))
	private Item filterItem;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreated() {
		this.createdAt = LocalDateTime.now();
	}
}
