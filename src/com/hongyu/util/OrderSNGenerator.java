package com.hongyu.util;

import java.util.concurrent.atomic.AtomicInteger;

public class OrderSNGenerator {
	static private Integer DailyMax = 99999999;
	static public AtomicInteger sn_order = new AtomicInteger(0);
	

	/** 订单SN */
	static public String getOrderSN(){
		int num=sn_order.incrementAndGet();
		if(sn_order.get()>=DailyMax){
			sn_order.set(0);
		}
		return String.format("%08d", num);
	}
}
