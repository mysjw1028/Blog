package site.metacoding.red.web.dto.request.users;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDto {	//Dto만들기
	private String username;
	private String password;
}
