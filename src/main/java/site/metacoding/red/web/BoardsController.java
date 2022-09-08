package site.metacoding.red.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import oracle.jdbc.proxy.annotation.Post;
import site.metacoding.red.domain.boards.Boards;
import site.metacoding.red.domain.boards.BoardsDao;
import site.metacoding.red.domain.users.Users;
import site.metacoding.red.web.dto.request.boards.UpdateDto;
import site.metacoding.red.web.dto.request.boards.WriteDto;
import site.metacoding.red.web.dto.response.boards.MainDto;
import site.metacoding.red.web.dto.response.boards.PagingDto;

@RequiredArgsConstructor
@Controller
public class BoardsController {

	private final HttpSession session;
	private final BoardsDao boardsDao;

	@PostMapping("/boards/{id}/update")
	public String update(@PathVariable Integer id, UpdateDto updateDto) {
		// 1영속화
		Boards boardsPS = boardsDao.findById(id);
		Users principal = (Users) session.getAttribute("principal");
		// 비정상 요청 체크
		if (boardsPS == null) {
			return "errors/badPage";
		}
		// 인증 체크
		if (principal == null) {
			return "redirect:/loginForm";
		}
		// 권한 체크 ( 세션 principal.getId() 와 boardsPS의 userId를 비교)
		if (principal.getId() != boardsPS.getUsersId()) {
			return "errors/badPage";
		}
		// 2변경
		boardsPS.글수정(updateDto);
		// 3 수행
		boardsDao.update(boardsPS);

		return "redirect:/boards/" + id;
	}

	@GetMapping("/boards/{id}/updateForm")
	public String updateForm(@PathVariable Integer id, Model model) {
		Boards boardsPS = boardsDao.findById(id);
		Users principal = (Users) session.getAttribute("principal");

		// 비정상 요청 체크
		if (boardsPS == null) {
			return "errors/badPage";
		}
		// 인증 체크
		if (principal == null) {
			return "redirect:/loginForm";
		}
		// 권한 체크 ( 세션 principal.getId() 와 boardsPS의 userId를 비교)
		if (principal.getId() != boardsPS.getUsersId()) {
			return "errors/badPage";
		}

		model.addAttribute("boards", boardsPS);

		return "boards/updateForm";
	}

	@PostMapping("/boards/{id}/delete") // 프라이머리 키라서 패스벨루로 받음 / 아닌걱는 쿼리스트링/동사는걸면 안된다.
	public String deleteBoards(@PathVariable Integer id) {
		Users principal = (Users) session.getAttribute("principal");
		Boards boardsPS = boardsDao.findById(id);
		if (boardsPS == null) {// if는 비정상 로직을 타게해서 걸러내는 필터 역할을 하는게 좋다.
			return "redirect:/boards/" + id;// 비정상 요청 체크
		}
		// 인증체크
		if (principal == null) {
			return "redirect:/loginForm";
		}
		// 권한체크 (세션 principal.getId() 와 boardsPS의 userId를 비교 )
		if (principal.getId() != boardsPS.getUsersId()) {
			return "redirect:/boards/" + id;
		}
		boardsDao.delete(id);// 핵심로직!/ 공통로직이 더 길고. 시간을 더 많이 잡아먹게된다. -> 공통로직은 분리가 될수있고 실행할수도있다.
		return "redirect:/";// 변경이 됬는지/ 이미 셀렉화되어있는지 영속화해서체크 트랙젝션을 안타는게 중요함
		// 영속화하는게좋음 ->트래젝션 때문에
	}

	@PostMapping("/boards")
	public String writeBoards(WriteDto writeDto) {
		// 1번 세션에 접근해서 세션값을 확인한다. 그때 Users로 다운캐스팅하고 키값은 principal로 한다.
		Users principal = (Users) session.getAttribute("principal");

		// 2번 pricipal null인지 확인하고 null이면 loginForm 리다이렉션해준다.
		if (principal == null) {
			return "redirect:/loginForm";
		}

		// 3번 BoardsDao에 접근해서 insert 메서드를 호출한다.
		// 조건 : dto를 entity로 변환해서 인수로 담아준다.
		// 조건 : entity에는 세션의 principal에 getId가 필요하다.
		boardsDao.insert(writeDto.toEntity(principal.getId()));

		return "redirect:/";
	}

	// http://localhost:8000/ -integer로 들어가서 null이기에 작동을 안함 0을 디폴트값으로 해양함
	// http://localhost:8000/?page=0, 1, 2를 해서 넣어준다.
	@GetMapping({ "/", "/boards" })
	public String getBoardDetail(Model model, Integer page) {// 사용자가 0 ->0, 1-> 10, 2->20를 날림 페이지x10을 하면된다.
		if (page == null)
			page = 0;// 한줄은 중괄호 안넣어줘도 된다.
		int startNum = page * 3;
		// paging.set머시기로 dto완성
		List<MainDto> boardsList = boardsDao.findAll(startNum);
		PagingDto paging = boardsDao.paging(page);

		final int blockCount = 5;
		int currentBlock = page / blockCount;
		int startPageNum = 1 + blockCount * currentBlock;
		int lastPageNum = 5 + blockCount * currentBlock;

		if (paging.getTotalCount() < lastPageNum) {
			lastPageNum = paging.getTotalPage();
		}
		paging.setBlockCount(blockCount);
		paging.setCurrentBlock(currentBlock);
		paging.setStartPageNum(startPageNum);
		paging.setLastPageNum(lastPageNum);

		model.addAttribute("boardsList", boardsList);
		model.addAttribute("paging", paging);// 쿼리가 boardsList. paging를 하나식 실행
		return "boards/main";
	}

	@GetMapping("/boards/{id}")
	public String getBoardList(@PathVariable Integer id, Model model) {
		model.addAttribute("boards", boardsDao.findById(id));
		return "boards/detail";
	}

	@GetMapping("/boards/writeForm")
	public String writeForm() {// 글쓰기는 항상 이공식 사용
		Users principal = (Users) session.getAttribute("principal");
		if (principal == null) {
			return "redirect:/loginForm";
		}
		return "boards/writeForm";
	}// 쳐내서 else문에 안걸리게 한다
}
