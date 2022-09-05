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
import site.metacoding.red.web.dto.request.boards.WriteDto;
import site.metacoding.red.web.dto.response.boards.MainDto;

@RequiredArgsConstructor
@Controller
public class BoardsController {

	private final HttpSession session;
	private final BoardsDao boardsDao;

	@PostMapping("/boards")
	public String writeBoards(WriteDto writeDto) {
		Users principal = (Users) session.getAttribute("principal");
		if (principal == null) {
			return "redirect/loginForm";
		}
		boardsDao.insert(writeDto.toEntity(principal.getId()));
		return "redirect:/";
	}

	// http://localhost:8000/ -integer로 들어가서 null이기에 작동을 안함 0을 디폴트값으로 해양함
	// http://localhost:8000/?page=0, 1, 2를 해서 넣어준다.
	@GetMapping({ "/", "/boards" })
	public String getBoardList(Model model, Integer page) {// 사용자가 0 ->0,1-> 10,2->20를 날림 페이지x10을 하면된다.
		if (page == null)
			page = 0;// 한줄은 중괄호 안넣어줘도 된다.
		int startNum = page * 10;
		List<MainDto> boardsList = boardsDao.findAll(startNum);
		model.addAttribute("boardsList", boardsList);
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
