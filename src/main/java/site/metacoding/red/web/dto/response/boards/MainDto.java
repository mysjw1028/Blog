package site.metacoding.red.web.dto.response.boards;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MainDto {
	private Integer id;
	private String title;
	private String username;
	private PagingDto Paging;//페이지는 다로 빼서 정리
}