package com.study.travly.board;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
	@Query("SELECT DISTINCT b FROM Board b JOIN FETCH b.places p JOIN FETCH b.member m JOIN FETCH p.files f WHERE b.id = :boardId")
	Optional<Board> findByIdWithPlaces(@Param("boardId") Long boardId);

	Optional<Board> findById(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
	int incrementViewCount(@Param("id") Long id);

	/**
	 * 필터링 조건과 페이징 정보를 사용하여 Board 목록을 조회합니다.
	 * Spring Data JPA가 Pageable을 인식하여 자동으로 COUNT 쿼리를 생성합니다.
	 * * @param itemIds 필터 아이템 ID 리스트
	 * @param pageable 페이징 정보 (페이지 번호, 크기, 정렬)
	 * @return Board 엔티티의 Page 객체
	 */
	@Query(value = """
			SELECT DISTINCT b FROM Board b JOIN BoardFilterItem bfi ON b.id = bfi.board.id
			WHERE bfi.filterItem.id IN :itemIds
					""", countQuery = """
			SELECT COUNT(DISTINCT b.id) FROM Board b JOIN BoardFilterItem bfi ON b.id = bfi.board.id
			WHERE bfi.filterItem.id IN :itemIds
			""")
	Page<Board> findBoardsByFilterItemIds(@Param("itemIds") List<Long> itemIds, Pageable pageable);

	/**
	 * 필터링 조건이 없을 경우의 전체 조회 메서드 (페이징만 적용)
	 */
	Page<Board> findAll(Pageable pageable);

	@Query(value = """
			WITH RankedPlace AS (
			    SELECT
			        bp.id,
			        bp.board_id,
			        bp.order_num,
			        bp.title,
			        ROW_NUMBER() OVER(PARTITION BY bp.board_id ORDER BY bp.order_num ASC) as rn_place
			    FROM board_place bp
			),
			RankedFile AS (
			    SELECT
			        bpf.id,
			        bpf.board_place_id,
			        bpf.order_num,
			        bpf.file_id,
			        ROW_NUMBER() OVER(PARTITION BY bpf.board_place_id ORDER BY bpf.order_num ASC) as rn_file
			    FROM board_place_file bpf
			)
			SELECT
			    b.id as id,
			    b.title as title,
			    rp.id AS placeId,
			    rp.title AS placeTitle,
			    f.filename AS placeFilename,
			    b.updated_at as updatedAt,
			    m.id AS memberId,
			    m.nickname AS memberNickname,
			    m.badge_id as badgeId,
			    (SELECT COUNT(*) FROM Likes l WHERE l.board_id = b.id) AS likeCount
			FROM Board b
			JOIN member m ON m.id = b.member_id
			LEFT JOIN RankedPlace rp ON rp.board_id = b.id AND rp.rn_place = 1
			LEFT JOIN RankedFile rf ON rf.board_place_id = rp.id AND rf.rn_file = 1
			LEFT JOIN file f ON f.id = rf.file_id
			ORDER BY b.updated_at DESC
			""", nativeQuery = true)
	Page<BoardListResponse> findBoardListWithFirstPlaceAndFile(Pageable pageable);
}