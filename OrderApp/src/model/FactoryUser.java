package model;

import java.util.Objects;

public class FactoryUser {
	
	@Override
	public int hashCode() {
		return Objects.hash(username);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FactoryUser other = (FactoryUser) obj;
		return Objects.equals(username, other.username);
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public FactoryUser(String username, String password) {
		this.username = username;
		this.password = password;
	}
	public FactoryUser() {
		// TODO Auto-generated constructor stub
	}
	private String username;
	private String password;
}
