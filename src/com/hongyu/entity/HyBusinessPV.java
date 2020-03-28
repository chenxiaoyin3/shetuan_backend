package com.hongyu.entity;

import java.util.Date;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_business_pv")
public class HyBusinessPV {
	private Long id;
	private Date clickTime;
	private WechatAccount wechatAccount;
	private WeBusiness weBusiness;
	private Integer clickType;
	private Long itemId;
	private Boolean isValid;
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Temporal(value=TemporalType.TIMESTAMP)
	@Column(name="click_time")
	public Date getClickTime() {
		return clickTime;
	}
	public void setClickTime(Date clickTime) {
		this.clickTime = clickTime;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="wechat_id")
	public WechatAccount getWechatAccount() {
		return wechatAccount;
	}
	public void setWechatAccount(WechatAccount wechatAccount) {
		this.wechatAccount = wechatAccount;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="webusiness_id")
	public WeBusiness getWeBusiness() {
		return weBusiness;
	}
	public void setWeBusiness(WeBusiness weBusiness) {
		this.weBusiness = weBusiness;
	}
	
	@Column(name="click_type")
	public Integer getClickType() {
		return clickType;
	}
	public void setClickType(Integer clickType) {
		this.clickType = clickType;
	}
	
	@Column(name="item_id")
	public Long getItemId() {
		return itemId;
	}
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	
	@Column(name="is_valid")
	public Boolean getIsValid() {
		return isValid;
	}
	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}
	

}
