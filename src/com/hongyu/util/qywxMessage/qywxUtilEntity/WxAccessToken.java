package com.hongyu.util.qywxMessage.qywxUtilEntity;

import java.io.Serializable;
/**
 * access_token的类
 * <br/>
 * 暂时用来存access_token相关的两个东西
 * <p>1. String accessToken 就是access_token的字符串<br/>
 * 2. long expiresTime 这里记录过期的时间戳</p>
 */
public class WxAccessToken implements Serializable {
	private static final long serialVersionUID = -124319321415912285L;
	
	private String accessToken;
	private long expiresTime;
	
	public boolean isAccessTokenExpired() {
	    return System.currentTimeMillis() > this.expiresTime;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public long getExpiresTime() {
		return expiresTime;
	}
	public void setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
	}
}
