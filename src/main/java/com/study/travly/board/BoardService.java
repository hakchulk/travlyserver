package com.study.travly.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.travly.board.BoardSaveRequest.BoardPlaceDto;
import com.study.travly.board.BoardSaveRequest.BoardPlaceFileDto;
import com.study.travly.board.filter.BoardFilterService;
import com.study.travly.board.like.LikeRepository;
import com.study.travly.board.place.BoardPlace;
import com.study.travly.board.place.file.BoardPlaceFile;
import com.study.travly.exception.BadRequestException;
import com.study.travly.file.File;
import com.study.travly.file.FileRepository;
import com.study.travly.filter.item.ItemRepository;
import com.study.travly.member.Member;
import com.study.travly.member.MemberRepository;

@Service
public class BoardService {
	@Autowired
	private BoardFilterService boardFilterService;

	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private MemberRepository memberRepository; // Member 조회용
	@Autowired
	private FileRepository fileRepository; // File 조회용
	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private ItemRepository itemRepository;

	/**
	 * JSON 요청 하나로 Board, BoardPlace, BoardPlaceFile을 모두 저장합니다.
	 */
	@Transactional
	public Optional<Board> saveBoardWithAllDetails(BoardSaveRequest request) {
		// Member 엔티티 조회 (FK 제약조건 만족을 위해 필요)
		Member member = memberRepository.findById(request.getMemberId()).orElseThrow(
				() -> new BadRequestException(String.format("존재하지 않는 member.id [%d]", request.getMemberId())));

		// Board 엔티티 생성 및 관계 설정
		Board board = Board.builder().title(request.getTitle()).member(member).build();
		// @PrePersist가 createdAt, updatedAt을 설정합니다.

		Set<BoardPlace> boardPlaces = new HashSet<>();
		int placeOrder = 0; // BoardPlace 순번 카운터

		if (request.getPlaces() != null) {
			for (BoardPlaceDto placeDto : request.getPlaces()) {
				boardPlaces.add(boardPlaceDto2BoardPlace(placeDto, board, placeOrder++));
			}
		}

		board.setPlaces(boardPlaces);
		Board newBoard = boardRepository.save(board);

		// filter 저장 @Transactional로 인해 saveBoardFilterItems()로 Transaction 으로 처리.
		boardFilterService.saveBoardFilterItems(newBoard.getId(), request.getFilterItemIds());
		// CascadeType.PERSIST 설정 덕분에 BoardPlace와 BoardPlaceFile도 함께 DB에 저장됩니다.
		return Optional.of(newBoard);
	}

	private BoardPlaceFile boardPlaceFileDto2BoardPlaceFile(BoardPlaceFileDto fileDto, BoardPlace boardPlace,
			int orderNum) {
		File file = fileRepository.findById(fileDto.getFileId())
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 file.id [%d]", fileDto.getFileId())));

		// BoardPlaceFile 객체 생성
		BoardPlaceFile boardPlaceFile = new BoardPlaceFile(null, // id
				boardPlace, // boardPlace (참조 설정)
				file, orderNum, // DTO에서 받은 orderNum 사용 (PrePersist에서 0으로 설정되는 문제 해결 필요)
				null // createdAt
		);

		return boardPlaceFile;
	}

	private BoardPlace boardPlaceDto2BoardPlace(BoardPlaceDto placeDto, Board board, int placeOrder) {
		BoardPlace boardPlace = new BoardPlace(null, // id
				board, // board (참조 설정)
				placeDto.getTitle(), placeDto.getContent(), placeOrder, // 순번 증가 (문제 없음)
				placeDto.getMapPlaceId(), placeDto.getExternalId(), placeDto.getX(), placeDto.getY(), null, null, // createdAt,
				// updatedAt
				null // files (BoardPlaceFile 리스트)
		);

		// 4. BoardPlaceFile 리스트 처리 (내부 For 루프)
		Set<BoardPlaceFile> boardPlaceFiles = new HashSet<>();
		int orderNum = 0;
		if (placeDto.getFiles() != null) {
			for (BoardPlaceFileDto fileDto : placeDto.getFiles()) {
				boardPlaceFiles.add(boardPlaceFileDto2BoardPlaceFile(fileDto, boardPlace, orderNum++));
			}
		}
		boardPlace.setFiles(boardPlaceFiles);

		return boardPlace;
	}

	@Transactional
	public Optional<Board> findByIdWithPlaces(Long id) {
		Optional<Board> opt = boardRepository.findByIdWithPlaces(id);
		if (!opt.isPresent())
			throw new BadRequestException(String.format("존재하지 않는 board.id [%d]", id));

		opt.ifPresent(board -> {
			boardRepository.incrementViewCount(id);
		});
		return opt;
	}

	public List<WeeklyTopBoardDTO> getWeeklyTopBoards(LocalDateTime start, LocalDateTime end) {

        // 1) 기본 데이터 조회
        List<WeeklyTopBoardTempDTO> baseList = boardRepository.findWeeklyTopBoards(start, end);

        if (baseList.isEmpty()) return List.of();

        // 2) 게시글 ID 리스트
        List<Long> ids = baseList.stream().map(WeeklyTopBoardTempDTO::getId).toList();

        // 3) 태그 전체 가져오기
        List<Object[]> tagRows = itemRepository.findTagsByBoardIds(ids);

        // 4) boardId → tags 매핑
        Map<Long, List<String>> tagMap = new HashMap<>();
        for (Object[] row : tagRows) {
            Long boardId = (Long) row[0];
            String tag = (String) row[1];
            tagMap.computeIfAbsent(boardId, k -> new ArrayList<>()).add(tag);
        }

        // 5) 최종 DTO 만들기
        return baseList.stream()
                .map(t -> WeeklyTopBoardDTO.builder()
                        .id(t.getId())
                        .title(t.getTitle())
                        .createdAt(t.getCreatedAt().toString())
                        .viewCount(t.getViewCount())
                        .memberId(t.getMemberId())
                        .memberName(t.getMemberName())
                        .badgeId(t.getBadgeId())
                        .profileImg(t.getProfileImg())
                        .likeCount(t.getLikeCount())
                        .content(t.getContent())
                        .tags(tagMap.getOrDefault(t.getId(), List.of()))
                        .build()
                ).toList();
    }

}
