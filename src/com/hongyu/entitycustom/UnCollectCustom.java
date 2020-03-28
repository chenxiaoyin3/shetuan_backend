package com.hongyu.entitycustom;

import java.util.Date;

/**
 * 未收款列表 
 * 用于返回前台list格式
 */
public class UnCollectCustom extends CollectOrPayBaseCustom{
	public Date date;
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
