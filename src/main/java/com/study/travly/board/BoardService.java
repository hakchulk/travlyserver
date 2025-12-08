package com.study.travly.board;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.travly.board.BoardSaveRequest.BoardPlaceFileDto;
import com.study.travly.board.filter.BoardFilterService;
import com.study.travly.board.place.BoardPlace;
import com.study.travly.board.place.BoardPlaceDto;
import com.study.travly.board.place.file.BoardPlaceFile;
import com.study.travly.exception.BadRequestException;
import com.study.travly.file.File;
import com.study.travly.file.FileRepository;
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

	public Optional<Board> findByIdWithPlaces(Long id) {
		Optional<Board> opt = boardRepository.findByIdWithPlaces(id);
		if (!opt.isPresent())
			throw new BadRequestException(String.format("존재하지 않는 board.id [%d]", id));

		opt.ifPresent(board -> {
			boardRepository.incrementViewCount(id);
		});
		return opt;
	}

}
