package com.hongyu.util.contract;
/**
 * 线路合同购物协议pojo
 * @author liyang
 * @version 2019年1月24日 下午4:26:04
 */

import java.util.Date;

public class ShoppingAgreement {
	/**购物时间*/
	private Date shoppingTime;
	/**购物最长停留时间*/
	private Integer duration;
	/**购物地点*/
	private String address;
	/**购物场所名称*/
	private String storeName;
	/**主要商品信息*/
	private String goodInfo;
	/**其他信息*/
	private String otherInfo;
	/**旅游者签名*/
	private String customerName;
	public Date getShoppingTime() {
		return shoppingTime;
	}
	public void setShoppingTime(Date shoppingTime) {
		this.shoppingTime = shoppingTime;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getGoodInfo() {
		return goodInfo;
	}
	public void setGoodInfo(String goodInfo) {
		this.goodInfo = goodInfo;
	}
	public String getOtherInfo() {
		return otherInfo;
	}
	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
}
