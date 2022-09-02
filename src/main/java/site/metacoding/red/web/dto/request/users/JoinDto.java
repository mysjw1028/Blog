package site.metacoding.red.web.dto.request.users;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinDto {	//Dto만들기
	private String username;
	private String password;
	private String email;
}
