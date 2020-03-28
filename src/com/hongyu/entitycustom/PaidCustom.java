package com.hongyu.entitycustom;

import java.util.Date;

/**
 * 已付款 
 * 继承自CollectOrPayBaseCustom 增加付款人和付款日期
 * */
public class PaidCustom extends CollectOrPayBaseCustom{
	private String payer;
	private Date date;
	
	public String getPayer() {
		return payer;
	}
	public void setPayer(String payer) {
		this.payer = payer;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
