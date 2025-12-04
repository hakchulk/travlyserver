package com.study.travly.board.comment;

import java.util.List;

import org.springframework.data.domain.Page; // 💡 Page 객체 import
import org.springframework.data.domain.Pageable; // 💡 Pageable 객체 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByBoardIdOrderByUpdatedAtDesc(Long boardId);

	// Board ID와 Pageable 객체를 인수로 받아 페이징된 결과를 Page<Comment>로 반환합니다.
	Page<Comment> findByBoardId(Long boardId, Pageable pageable);
}