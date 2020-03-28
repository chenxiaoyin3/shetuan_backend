package com.hongyu.util;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderTransactionSNGenerator {
	static private Integer DailyMax = 99999;
	static public AtomicInteger deal_sn = new AtomicInteger(0);
	static public AtomicInteger refund_sn = new AtomicInteger(0);
	
	/** 交易记录 SN */
	static public String getSN(boolean isDeal) {
		if (isDeal) {
			if (deal_sn.get() >= DailyMax) {
				deal_sn.set(0);
			}
			int num = deal_sn.incrementAndGet();
			Calendar current = Calendar.getInstance();
			int year = current.get(Calendar.YEAR);
			int month = current.get(Calendar.MONTH)+1;
			int day = current.get(Calendar.DAY_OF_MONTH);
			return String.format("%04d", year)+String.format("%02d", month)+String.format("%02d", day)+String.format("%01d", 1)+String.format("%05d", num);
		} else {
			if (refund_sn.get() >= DailyMax) {
				refund_sn.set(0);
			}
			int num = refund_sn.incrementAndGet();
			Calendar current = Calendar.getInstance();
			int year = current.get(Calendar.YEAR);
			int month = current.get(Calendar.MONTH)+1;
			int day = current.get(Calendar.DAY_OF_MONTH);
			return String.format("%04d", year)+String.format("%02d", month)+String.format("%02d", day)+String.format("%01d", 2)+String.format("%05d", num);
		}
	}
}
