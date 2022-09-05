package site.metacoding.red.web;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import oracle.jdbc.proxy.annotation.Post;
import site.metacoding.red.domain.boards.Boards;
import site.metacoding.red.domain.boards.BoardsDao;
import site.metacoding.red.domain.users.Users;
import site.metacoding.red.web.dto.request.boards.WriteDto;

@RequiredArgsConstructor
@Controller
public class BoardsController {

	private final HttpSession session;
	private final BoardsDao boardsDao;

	@PostMapping("/boards")
	public String writeBoard(WriteDto writeDto) {// 동사를 먼저 적고 명사를 적는게 좋다 -> 반대로하면 나중에 개판이 된다/클라이언트와 통신할때 씀
		// 1번 세션으로 접근해서 세션값을 확인한다. 그때 Users로 다운캐스팅하고 키값은principal로 한다.
		Users principal = (Users) session.getAttribute("principal");
		// 2번 principal이 null인지 확인하고 null이면 loginForm redirect해준다.
		if (principal == null) {
			return "redirect/loginForm";
		}
		// 3번 BoardsDao에 접근해서 insert메서드를 호출한다.
		boardsDao.insert(writeDto.toEntity(principal.getId()));
		return "redirect:/";
		// 조건 : dto를 entity호 변환해서 인수로 담아준다.
		// 조건 : entity에는 세션의 principal에 getId가 필요하다 // 되게 좋다...

	}

	@GetMapping({ "/", "/boards" })
	public String getBoardList() {
		return "boards/main";
	}

	@GetMapping("/boards/{id}")
	public String getBoardList(@PathVariable Integer id) {
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
