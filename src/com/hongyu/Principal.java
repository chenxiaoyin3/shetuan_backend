package com.hongyu;

import java.io.Serializable;

/**
 * 身份信息
 * 
 */
@SuppressWarnings("serial")
public class Principal implements Serializable {
	


	public enum UserType{
		admin,
		expert,
		depot,
		factory,
		farmer,
		invest,
	};
	
	/** ID */
	private UserType type;

	/** 用户名 */
	private String username;

	/**
	 * @param id
	 *            ID
	 * @param username
	 *            用户名
	 */
	public Principal(UserType type, String username) {
		this.type = type;
		this.username = username;
	}


	public UserType getType() {
		return type;
	}


	public void setType(UserType type) {
		this.type = type;
	}


	/**
	 * 获取用户名
	 * 
	 * @return 用户名
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置用户名
	 * 
	 * @param username
	 *            用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}

}