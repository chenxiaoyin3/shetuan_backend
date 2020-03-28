package com.hongyu.util;

import java.util.concurrent.atomic.AtomicInteger;

public class CouponSNGenerator {
	static private Integer DailyMax = 99999;
	static public AtomicInteger sn_line = new AtomicInteger(0);
	static public AtomicInteger sn_award = new AtomicInteger(0);
	static public AtomicInteger sn_sale = new AtomicInteger(0);
	static public AtomicInteger sn_gift = new AtomicInteger(0);
	static public AtomicInteger sn_bigcustomer = new AtomicInteger(0);
	static public AtomicInteger sn_sale_order = new AtomicInteger(0);

	/** 销售奖励电子券 SN */
	static public String getAwardSN() {
		int num = sn_award.incrementAndGet();
		if (sn_award.get() >= DailyMax) {
			sn_award.set(0);
		}
		return String.format("%05d", num);
	}

	/** 商城销售电子券 SN */
	static public String getSaleSN() {
		int num = sn_sale.incrementAndGet();
		if (sn_sale.get() >= DailyMax) {
			sn_sale.set(0);
		}
		return String.format("%05d", num);
	}

	/** 商城赠送电子券 */
	static public String getGiftSN() {
		int num = sn_gift.incrementAndGet();
		if (sn_gift.get() >= DailyMax) {
			sn_gift.set(0);
		}
		return String.format("%05d", num);
	}

	/** 大客户购买电子券 SN */
	static public String getBigCustomerSN() {
		int num = sn_bigcustomer.incrementAndGet();
		if (sn_bigcustomer.get() >= DailyMax) {
			sn_bigcustomer.set(0);
		}
		return String.format("%05d", num);
	}

}
