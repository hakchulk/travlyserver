package com.study.travly.board.bookmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.travly.board.Board;
import com.study.travly.board.BoardRepository;
import com.study.travly.exception.BadRequestException;
import com.study.travly.member.Member;
import com.study.travly.member.MemberRepository;

@Service
public class BookmarkService {
	@Autowired
	BookmarkRepository bookmarkRepository;

	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private MemberRepository memberRepository; // Member 조회용

	public Bookmark create(Long boardId, Long memberId) {
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 board.id [%d]", boardId)));

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 member.id [%d]", memberId)));

		boolean b = bookmarkRepository.existsByBoardIdAndMemberId(boardId, memberId);
		if (b)
			throw new BadRequestException(
					String.format("Bookmark 에 board.id [%d], member.id [%d] 가 이미 존재 합니다.", boardId, memberId));

		Bookmark bookmark = new Bookmark(null, board, member, null);
		bookmark = bookmarkRepository.save(bookmark);

		return bookmark;
	}

	@Transactional // 트랜잭션 내에서 DELETE 쿼리를 실행해야 합니다.
	public boolean delete(Long boardId, Long memberId) {
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 board.id [%d]", boardId)));

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException(String.format("존재하지 않는 member.id [%d]", memberId)));

		boolean b = bookmarkRepository.existsByBoardIdAndMemberId(boardId, memberId);
		if (!b)
			throw new BadRequestException(
					String.format("Bookmark 에 board.id [%d], member.id [%d] 가 존재 하지 않습니다..", boardId, memberId));

		// Repository 메서드 호출: 바로 데이터베이스에 DELETE 명령을 보냅니다.
		int deletedCount = bookmarkRepository.deleteByBoardIdAndMemberId(boardId, memberId);

		// 삭제된 행의 수가 1 이상이면 성공으로 간주
		return deletedCount > 0;
	}

}
