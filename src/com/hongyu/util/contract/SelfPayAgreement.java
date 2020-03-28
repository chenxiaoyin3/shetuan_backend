package com.hongyu.util.contract;
/**
 * 线路合同自愿付费项目协议
 * @author liyang
 * @version 2019年1月24日 下午4:33:06
 */

import java.math.BigDecimal;
import java.util.Date;

public class SelfPayAgreement {
	/**自费项目开始时间*/
	private Date time;
	/**自费项目地点*/
	private String address;
	/**自费项目名称以及描述*/
	private String  itemDescription;
	/**自费项目费用*/
	private BigDecimal money;
	/**自费项目最长持续时间 /分钟*/
	private String duration;
	/**自费项目其他描述*/
	private String otherInfo;
	/**自费项目游客签署姓名*/
	private String customerName;
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
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
