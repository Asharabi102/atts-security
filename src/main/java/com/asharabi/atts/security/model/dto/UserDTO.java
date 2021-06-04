package com.asharabi.atts.security.model.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {

	private static final long serialVersionUID = 4708793901475556163L;

	private int id;
	private String username;
	private String password;
	private String email;
	private String resetPasswordToken;

	public UserDTO() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	
	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

}