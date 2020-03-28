package com.hongyu.util;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class PurchaseSnGenerator {
	
	static public AtomicInteger sn = new AtomicInteger(0);
	
	static public String getSN(long num) {
		Calendar current = Calendar.getInstance();
		int year = current.get(Calendar.YEAR);
		int month = current.get(Calendar.MONTH)+1;
		int day = current.get(Calendar.DAY_OF_MONTH);
		return String.format("%04d", year)+String.format("%02d", month)+String.format("%02d", day)+String.format("%04d", num);
	}
}
