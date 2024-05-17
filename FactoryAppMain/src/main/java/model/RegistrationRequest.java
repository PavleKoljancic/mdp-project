package model;

import java.io.Serializable;

public class RegistrationRequest implements Serializable{
	 private String companyName;
	 private String address;
	 private String phoneNumber;
	 private String username;
	 private String password;
	 private String passwordRepeat;

	 public RegistrationRequest(String companyName, String address, String phoneNumber, String username, String password,
			String passwordRepeat) {
		super();
		this.companyName = companyName;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.username = username;
		this.password = password;
		this.passwordRepeat = passwordRepeat;

	}
	public RegistrationRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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
	public String getPasswordRepeat() {
		return passwordRepeat;
	}
	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}
	
	
	public boolean checkPasswordMatch() 
	{
		if(password!=null)
			return password.equals(passwordRepeat);
		return false;
	}
	
	public User buildUser()  
	{
		return new User(companyName, address, phoneNumber, username, password, false,false);
	}
}
