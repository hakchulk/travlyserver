package com.study.travly.filter.category;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.Comment;

import com.study.travly.filter.item.Item;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "filter_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Comment("filter item을 다중 선택 갯수. 1이면 다중 선택 불가")
	@Column(nullable = false)
	private int multiSelectCount;

	@Column(nullable = false)
	private int orderNum;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreated() {
		this.createdAt = LocalDateTime.now();
	}

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	@OrderBy("orderNum ASC")
	private Set<Item> items;
}
