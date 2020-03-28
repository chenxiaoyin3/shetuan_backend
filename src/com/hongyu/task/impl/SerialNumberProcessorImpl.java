package com.hongyu.task.impl;

import org.springframework.stereotype.Component;

import com.hongyu.task.Processor;
import com.hongyu.util.CouponSNGenerator;
import com.hongyu.util.OrderSNGenerator;
import com.hongyu.util.PurchaseSnGenerator;
import com.hongyu.util.SpecialtySNGenerator;

@Component("serialNumberProcessor")
public class SerialNumberProcessorImpl implements Processor {
	
	//序列号置0
	@Override
	public void process() {
		CouponSNGenerator.sn_line.set(0);
		CouponSNGenerator.sn_award.set(0);
		CouponSNGenerator.sn_bigcustomer.set(0);
		CouponSNGenerator.sn_gift.set(0);
		CouponSNGenerator.sn_sale.set(0);
		CouponSNGenerator.sn_sale_order.set(0);
		PurchaseSnGenerator.sn.set(0);
		OrderSNGenerator.sn_order.set(0);
		SpecialtySNGenerator.sn_code.set(0);
	}

}
