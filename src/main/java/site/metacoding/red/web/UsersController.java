package site.metacoding.red.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import site.metacoding.red.domain.users.Users;
import site.metacoding.red.domain.users.UsersDao;
import site.metacoding.red.web.dto.request.users.JoinDto;
import site.metacoding.red.web.dto.request.users.LoginDto;

@RequiredArgsConstructor // 디펜더시 인젝션 코드
@Controller
public class UsersController {
	private final HttpSession session; //스프링이  서버시작시에 IOC 컨테이너에 보관함.
	private final UsersDao usersDao;

	@GetMapping("/logout")
	public String logout() {
		System.out.println("fdsaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		session.invalidate();//내 키값에 있는 데이터를 날려버림
		return "redirect:/";//해당사용자의 키값만 그 사람만 영역만 
	}
	
	@PostMapping("/login")// 로그인만 예외로 select인데 post로 함
	public String login(LoginDto loginDto) {
		Users usersPS = usersDao.login(loginDto);//PS를 붙여서 구분해야한다 
		if(usersPS != null) {//로그인인증
			session.setAttribute("principal", usersPS);//principal인증된유저로 사용됨/ 일반적으로 적는다. 있으면 로그인이 된거
			return "redirect:/";//boards의 메인
		}else {//인증안됨
			return "redirect:/loginForm";
		}	
	}

	@PostMapping("/join")
	public String join(JoinDto joinDto) {
		usersDao.insert(joinDto);
		return "redirect:/loginForm";// 미리만들어진거 쓰기
	}

	@GetMapping("/loginForm")
	public String loginForm() {
		return "users/loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "users/joinForm";
	}
}
