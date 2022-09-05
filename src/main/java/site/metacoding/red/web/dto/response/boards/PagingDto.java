package site.metacoding.red.web.dto.response.boards;

public class PagingDto {
	private Integer startNum;
	private Integer totalCount;//토탈카운트를 알아야 토탈페이지가 나옴다.
	private Integer totalPage;//23/10 = 2 / 나머지느 +1
	private Integer currentPage;
	private boolean isList;
	private boolean isFirst;
}
