package pl.com.jolszew.chat_server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chat_users")
public class ChatUsers {

	@Id
	@GeneratedValue
	private int id;
	
	private String nickname;

	public int getId() {
		return id;
	}

	public ChatUsers setId(int id) {
		this.id = id;
		return this;
	}

	public String getUsername() {
		return nickname;
	}

	public ChatUsers setNickname(String username) {
		this.nickname = username;
		return this;
	}
	
	
}
