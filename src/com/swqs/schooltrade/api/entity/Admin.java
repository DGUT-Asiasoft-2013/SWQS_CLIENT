package com.swqs.schooltrade.api.entity;

import java.io.Serializable;

public class Admin implements Serializable {
	String account;
	String passwordHash;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	
}
