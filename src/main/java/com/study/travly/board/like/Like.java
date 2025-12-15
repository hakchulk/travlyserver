package com.study.travly.board.like;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
// like는 키워드이므로 likes 사용
@Table(name = "likes", uniqueConstraints = {
		@UniqueConstraint(name = "UK_likes__board_member", columnNames = { "board_id", "member_id" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne()
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_likes__member_id"))
	private Member member;
	
	@ManyToOne()
	@JoinColumn(name = "board_id", nullable = false, foreignKey = @ForeignKey(name = "fk_likes__board_id"))
	private Board board;
	
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreated() {
		this.createdAt = LocalDateTime.now();
	}
	

	//	@Override
	//	public boolean equals(Object o) {
	//		// 동일 인스턴스인지 확인
	//		if (this == o)
	//			return true;
	//		// null이거나 클래스가 다르면 false
	//		if (o == null || getClass() != o.getClass())
	//			return false;
	//
	//		Like like = (Like) o;
	//
	//		return Objects.equals(member.getId(), like.getMember().getId())
	//				&& Objects.equals(board.getId(), like.getBoard().getId());
	//	}
	//
	//	// 3. 🌟 hashCode() 구현: equals()가 true인 객체는 반드시 같은 해시 코드를 반환해야 함
	//	@Override
	//	public int hashCode() {
	//		// 키 필드들을 인자로 사용하여 해시 코드를 생성
	//		// Objects.hash()를 사용하는 것이 가장 일반적이고 권장되는 방법입니다.
	//		return Objects.hash(member.getId(), board.getId());
	//	}
}
