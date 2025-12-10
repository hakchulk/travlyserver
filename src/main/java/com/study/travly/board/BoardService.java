package com.study.travly.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.travly.board.BoardSaveRequest.BoardPlaceFileDto;
import com.study.travly.board.filter.BoardFilterService;
import com.study.travly.board.like.LikeRepository;
import com.study.travly.board.place.BoardPlace;
import com.study.travly.board.place.BoardPlaceDto;
import com.study.travly.board.place.file.BoardPlaceFile;
import com.study.travly.exception.BadRequestException;
import com.study.travly.file.File;
import com.study.travly.file.FileRepository;
import com.study.travly.file.FileService;
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
	@Autowired
	private FileService fileService;

	private final String IMAGE_BASE_URL = "http://localhost:8080/api/travly/file/";
	
	public Page<BoardListResponse> getBoardList(BoardListRequest req, Pageable pageable) {
		//		return boardRepository.findBoardListWithFirstPlaceAndFile(pageable);
		return boardRepository.findBoardList(req.getItemIds(), pageable);
	}

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

	@Transactional
	public Optional<Board> updateBoardWithAllDetails(Long boardId, BoardUpdateRequest request) {
		// boardId 로 기존 board 조회
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 board.id [%d]", boardId)));

		board.setTitle(request.getTitle());

		Set<BoardPlace> boardPlaces = new HashSet<>();
		int placeOrder = 0; // BoardPlace 순번 카운터

		if (request.getPlaces() != null) {
			for (BoardPlaceDto placeDto : request.getPlaces()) {
				boardPlaces.add(boardPlaceDto2BoardPlace(placeDto, board, placeOrder++));
			}
		}

		board.setPlaces(boardPlaces);
		// @Transactional 로 인하여 boardRepository.save(board) 는 필요 없음.

		// filter 저장 @Transactional로 인해 saveBoardFilterItems()로 Transaction 으로 처리.
		boardFilterService.saveBoardFilterItems(boardId, request.getFilterItemIds());

		return Optional.of(board);
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
	
	
	//주간 인기글 TOP3
	public List<WeeklyTopBoardDTO> getWeeklyTopBoards(LocalDateTime start, LocalDateTime end) {

        // 1) 기본 데이터 조회 (DB에서 TempDTO 형태로 파일 ID를 포함하여 가져옴)
		List<WeeklyTopBoardTempDTO> baseList = boardRepository.findWeeklyTopBoards(start, end);

	    if (baseList.isEmpty()) return List.of();

	    // 2) 게시글 ID 리스트 추출
	    List<Long> ids = baseList.stream().map(WeeklyTopBoardTempDTO::getId).toList();

	    // 3) 태그 전체 가져오기 (성능을 위해 한 번에 조회)
	    List<Object[]> tagRows = itemRepository.findTagsByBoardIds(ids);

	    // 4) boardId → tags 매핑 Map 생성
	    Map<Long, List<String>> tagMap = new HashMap<>();
	    for (Object[] row : tagRows) {
	        Long boardId = (Long) row[0];
	        String tag = (String) row[1];
	        tagMap.computeIfAbsent(boardId, k -> new ArrayList<>()).add(tag);
	    }

	 // 5) 최종 DTO 만들기 (TempDTO -> DTO 변환 및 URL 생성)
	    return baseList.stream()
	            .map(t ->{ 
	                
	                // ===========================================
	                // 1. 게시글 대표 이미지 (cardImg) URL 생성 로직 (⭐ 원본 파일명 사용으로 수정)
	                // ===========================================
	                Long cardFileId = t.getCardImg();
	                String cardImgUrl = null; 

	                if (cardFileId != null) {
	                    String originalFileNameFromDB = fileService.getFilenameById(cardFileId); 
	                    
	                    if (originalFileNameFromDB != null) {
	                        
	                        // ⭐⭐⭐ 핵심 수정: 썸네일 관련 로직 제거 ⭐⭐⭐
	                        // 1. Base Name 추출 로직 불필요
	                        // 2. t_ 접두사와 .jpg 확장자 추가 불필요
	                        
	                        // 난수 파일명 전체(원본 파일의 확장자 포함)를 바로 사용합니다.
	                    	cardImgUrl = IMAGE_BASE_URL + originalFileNameFromDB; 
	                    }
	                }

	                // ===========================================
	                // 2. 회원 프로필 이미지 (profileImg) URL 생성 로직 (썸네일 유지)
	                // ===========================================
	                Long profileFileId = t.getProfileImg();
	                String profileImgUrl = null;

	                if (profileFileId != null) {
	                    
	                    String originalProfileFileName = fileService.getFilenameById(profileFileId); 
	                    
	                    if (originalProfileFileName != null) {
	                        
	                        // 썸네일 규칙을 적용하기 위해 Base Name 추출은 그대로 유지합니다.
	                        int lastDot = originalProfileFileName.lastIndexOf('.');
	                        String fileNameBase = (lastDot > 0) 
	                                ? originalProfileFileName.substring(0, lastDot) 
	                                : originalProfileFileName;
		                    
		                    // 썸네일 규칙 적용: t_ 접두사와 .jpg 확장자 유지
		                    String actualThumbnailFileName = "t_" + fileNameBase + ".jpg"; 
		                    
		                    profileImgUrl = IMAGE_BASE_URL + actualThumbnailFileName; 
	                    }
	                }

	                // ===========================================
	                // 3. 최종 DTO 빌더
	                // ===========================================
	            	return WeeklyTopBoardDTO.builder()
	                    .id(t.getId())
	                    .title(t.getTitle())
	                    .createdAt(t.getCreatedAt().toString())
	                    .viewCount(t.getViewCount())
	                    .memberId(t.getMemberId())
	                    .memberName(t.getMemberName())
	                    .badgeId(t.getBadgeId())
	                    .profileImg(profileImgUrl) // ⭐ 변환된 프로필 URL
	                    .likeCount(t.getLikeCount())
	                    .content(t.getContent())
	                    .tags(tagMap.getOrDefault(t.getId(), List.of()))
	                    .cardImg(cardImgUrl) // ⭐ 변환된 대표 이미지 URL
	                    .build();
	            })
	            // List<WeeklyTopBoardDTO>로 최종 변환
	            .collect(Collectors.toList()); 
	}


	
	//최근 게시글 9항목
	public List<RecentBoardDTO> getRecentBoards() {
    
    // 1) 기본 데이터 조회
    List<RecentBoardTempDTO> baseList = boardRepository.findRecentBoards();

    if (baseList.isEmpty()) return List.of();

    // 2) 게시글 ID 리스트
    List<Long> ids = baseList.stream().map(RecentBoardTempDTO::getId).toList();

    // 3) 태그 전체 가져오기
    List<Object[]> tagRows = itemRepository.findTagsByBoardIds(ids); 

    // 4) boardId → tags 매핑 (⭐ Map 생성 로직 추가)
    Map<Long, List<String>> tagMap = new HashMap<>();
    for (Object[] row : tagRows) {
        // Object[]에서 Long과 String으로 안전하게 형변환 (타입은 DB/JPA 설정에 따라 달라질 수 있음)
        Long boardId = (Long) row[0]; 
        String tag = (String) row[1];
        tagMap.computeIfAbsent(boardId, k -> new ArrayList<>()).add(tag);
    }

    // 5) 최종 RecentBoardDTO 만들기 (URL 변환 로직 포함)
    return baseList.stream()
            .map(t -> {
                
                // ===========================================
                // 1. 게시글 대표 이미지 (cardImg) URL 생성 (썸네일)
                // ===========================================
                Long cardFileId = t.getCardImg();
                String cardImgUrl = null; 

                if (cardFileId != null) {
                    String originalFileNameFromDB = fileService.getFilenameById(cardFileId); 
                    
                    if (originalFileNameFromDB != null) {
                        int lastDot = originalFileNameFromDB.lastIndexOf('.');
                        String fileNameBase = (lastDot > 0) ? originalFileNameFromDB.substring(0, lastDot) : originalFileNameFromDB;
                        
                        String actualThumbnailFileName = "t_" + fileNameBase + ".jpg"; 
                        cardImgUrl = IMAGE_BASE_URL + actualThumbnailFileName; 
                    }
                }

                // ===========================================
                // 2. 회원 프로필 이미지 (profileImg) URL 생성 (썸네일)
                // ===========================================
                Long profileFileId = t.getProfileImg();
                String profileImgUrl = null;

                if (profileFileId != null) {
                    
                    String originalProfileFileName = fileService.getFilenameById(profileFileId); 
                    
                    if (originalProfileFileName != null) {
                        
                        int lastDot = originalProfileFileName.lastIndexOf('.');
                        String fileNameBase = (lastDot > 0) ? originalProfileFileName.substring(0, lastDot) : originalProfileFileName;
                        
                        String actualThumbnailFileName = "t_" + fileNameBase + ".jpg"; 
                        profileImgUrl = IMAGE_BASE_URL + actualThumbnailFileName; 
                    }
                }

                // ===========================================
                // 3. 최종 DTO 빌더 (⭐ 누락된 필드 매핑 추가)
                // ===========================================
                return RecentBoardDTO.builder()
                        .id(t.getId())
                        .title(t.getTitle())
                        .createdAt(t.getCreatedAt().toString()) // LocalDateTime -> String
                        .viewCount(t.getViewCount())
                        .memberId(t.getMemberId())
                        .memberName(t.getMemberName())
                        .badgeId(t.getBadgeId())
                        .likeCount(t.getLikeCount())
                        .content(t.getContent())
                        // ⭐ 변환된 URL 사용
                        .profileImg(profileImgUrl) 
                        .cardImg(cardImgUrl)       
                        // ⭐ 태그 정보 매핑
                        .tags(tagMap.getOrDefault(t.getId(), List.of()))
                        .build();
            })
            .collect(Collectors.toList()); // ⭐ List<RecentBoardDTO> 반환 타입 지정
}

}
	

