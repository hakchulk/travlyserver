package com.study.travly.board;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	@Query("SELECT DISTINCT b FROM Board b JOIN FETCH b.places p JOIN FETCH b.member m JOIN FETCH p.files f WHERE b.id = :boardId")
	Optional<Board> findByIdWithPlaces(@Param("boardId") Long boardId);

	@Modifying
	@Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
	int incrementViewCount(@Param("id") Long id);

	//주간 게시글 TOP3
	@Query(value = """
		    SELECT 
		        b.id AS id,
		        b.title AS title,
		        b.created_at AS createdAt,
		        b.view_count AS viewCount,
		        m.id AS memberId,
		        m.name AS memberName,
		        ba.id AS badgeId,
		        m.file_id AS profileImg,
		        (SELECT COUNT(*) FROM likes l WHERE l.board_id = b.id) AS likeCount,
		        (SELECT bp.content
		         FROM board_place bp
		         WHERE bp.board_id = b.id
		         ORDER BY bp.order_num ASC
		         LIMIT 1) AS content,
		        (
		            -- ⭐ 썸네일 난수명(f.filename) 대신 파일 ID(f.id)를 가져옵니다.
		            SELECT f.id AS cardFileId 
		            FROM board_place bp
		            JOIN board_place_file bpf ON bp.id = bpf.board_place_id
		            JOIN file f ON bpf.file_id = f.id
		            WHERE bp.board_id = b.id
			            AND f.org_filename IS NOT NULL
		            ORDER BY bp.order_num ASC, bpf.order_num ASC
		            LIMIT 1
		        ) AS cardImg -- AS 별칭은 그대로 cardImg를 사용합니다.
		    FROM board b
		    JOIN member m ON b.member_id = m.id
		    LEFT JOIN badge ba ON m.badge_id = ba.id
		    WHERE b.created_at BETWEEN :start AND :end
		    ORDER BY b.view_count DESC
		    LIMIT 10
		""", nativeQuery = true)
		List<WeeklyTopBoardTempDTO> findWeeklyTopBoards(
		    @Param("start") LocalDateTime start,
		    @Param("end") LocalDateTime end
		);

	//최근 게시글 9
	@Query(value = """
		    SELECT 
		        b.id AS id,
		        b.title AS title,
		        b.created_at AS createdAt, 
		        b.view_count AS viewCount,
		        m.id AS memberId,
		        m.name AS memberName,
		        ba.id AS badgeId,
		        m.file_id AS profileImage,
		        (SELECT COUNT(*) FROM likes l WHERE l.board_id = b.id) AS likeCount,
		        (SELECT bp.content
		         FROM board_place bp
		         WHERE bp.board_id = b.id
		         ORDER BY bp.order_num ASC
		         LIMIT 1) AS content
		    FROM board b
		    JOIN member m ON b.member_id = m.id
		    LEFT JOIN badge ba ON m.badge_id = ba.id
		    -- WHERE b.created_at BETWEEN :start AND :end
		    ORDER BY b.created_at DESC 
		    LIMIT 9 
		""", nativeQuery = true)
	    // 💡 메서드 이름 변경 및 반환 타입 지정
	    List<RecentBoardTempDTO> findRecentBoards();

	
	
}