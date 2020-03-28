package com.hongyu.util;

import java.util.concurrent.atomic.AtomicInteger;

public class SpecialtySNGenerator {
	static private Integer DailyMax = 99999;
	static public AtomicInteger sn_code = new AtomicInteger(0);
	

	/** 订单SN */
	static public String getOrderSN(long categoryid, long num){
		return String.format("%05d", categoryid) + String.format("%05d", num);
	}
}
