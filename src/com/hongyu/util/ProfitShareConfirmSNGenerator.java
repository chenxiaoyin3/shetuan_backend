package com.hongyu.util;

import java.util.Calendar;

public class ProfitShareConfirmSNGenerator {
	/** 订单SN */
	static public String getConfirmSN(long num){
		Calendar current = Calendar.getInstance();
		int year = current.get(Calendar.YEAR);
		int month = current.get(Calendar.MONTH)+1;
		int day = current.get(Calendar.DAY_OF_MONTH);
		return String.format("%04d", year)+String.format("%02d", month)+String.format("%02d", day)+String.format("%06d", num);
	}
}
