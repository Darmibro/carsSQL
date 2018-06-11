package telran.security;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
public class AccountMongo {
	@Id
	String username;
	String password;
	String[] roles;

	public AccountMongo() {
	}

	public AccountMongo(String username, String password, String[] roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String getUsername() {
		return username;
	}

}
