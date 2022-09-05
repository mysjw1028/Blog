package site.metacoding.red.domain.boards;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Boards {
	private Integer id;
	private String title;
	private String content;
	private Integer userId;
	private Timestamp createdAt;
	
	public Boards(String title, String content, Integer usersId) {
		this.title = title;
		this.content = content;
		this.userId = usersId;
	}
}
