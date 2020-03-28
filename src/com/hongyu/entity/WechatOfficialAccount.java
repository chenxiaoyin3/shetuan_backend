package com.hongyu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="hy_wechat_official_account")
public class WechatOfficialAccount {
	private Long id;
	private String appId;
	private String mchId;
	private String apiKey;
	private String xcxAppId;
	private Boolean isValid;
	public WechatOfficialAccount() {
		super();
	}
	public WechatOfficialAccount(Long id, String appId, String mchId, String apiKey, String xcxAppId,Boolean isValid) {
		super();
		this.id = id;
		this.appId = appId;
		this.mchId = mchId;
		this.apiKey = apiKey;
		this.xcxAppId = xcxAppId;
		this.isValid = isValid;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",unique=true,nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="app_id")
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	@Column(name="mch_id")
	public String getMchId() {
		return mchId;
	}
	public void setMchId(String mchId) {
		this.mchId = mchId;
	}
	
	@Column(name="api_key")
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	@Column(name="is_valid")
	public Boolean getIsValid() {
		return isValid;
	}
	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}
	
	@Column(name="xcx_app_id")
	public String getXcxAppId() {
		return xcxAppId;
	}
	public void setXcxAppId(String xcxAppId) {
		this.xcxAppId = xcxAppId;
	}
	
	

}
