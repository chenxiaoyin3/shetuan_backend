package com.hongyu.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.CouponAward;
import com.hongyu.entity.CouponBigCustomer;
import com.hongyu.entity.CouponGift;
import com.hongyu.entity.CouponLine;
import com.hongyu.entity.CouponSale;
import com.hongyu.service.CouponAwardService;
import com.hongyu.service.CouponBigCustomerService;
import com.hongyu.service.CouponGiftService;
import com.hongyu.service.CouponLineService;
import com.hongyu.service.CouponSaleService;
import com.hongyu.task.Processor;

/**电子券-定时扫描-修改电子券状态  0:未绑定    1:已绑定     2:冻结   3:已过期   */
@Component("couponProcessor")
public class CouponProcessor implements Processor {
	@Resource(name = "couponBigCustomerServiceImpl")
	CouponBigCustomerService couponBigCustomerService;
	
	@Resource(name = "couponSaleServiceImpl")
	CouponSaleService couponSaleService;
	
	@Resource(name = "couponLineServiceImpl")
	CouponLineService couponLineService;
	
	@Resource(name = "couponAwardServiceImpl")
	CouponAwardService couponAwardService;
	
	@Resource(name = "couponGiftServiceImpl")
	CouponGiftService couponGiftService;
	
	@Override
	public void process() {
		
		System.out.println("CouponProcessor扫描");
		
		Date date = new Date();
		
		List<Filter> filters = new ArrayList<>();
		filters.add(new Filter("expireTime", Operator.le, date));
		filters.add(Filter.eq("state", 0));
		
		
		// 大客户购买电子券 
		List<CouponBigCustomer> list_bigCustomer = couponBigCustomerService.findList(null, filters, null);
		for(CouponBigCustomer couponBigCustomer : list_bigCustomer){
			couponBigCustomer.setState(3);// 置为已过期
			couponBigCustomerService.update(couponBigCustomer);
		}
		
		// 商城销售电子券
		List<CouponSale> list_sale = couponSaleService.findList(null, filters, null);
		for(CouponSale couponSale : list_sale){
			couponSale.setState(3);// 置为已过期
			couponSaleService.update(couponSale);
		}
		
		// 销售奖励电子券    直接绑定  不考虑过期?
		List<CouponAward> list_award = couponAwardService.findList(null, filters, null);
		for(CouponAward couponAward : list_award){
			couponAward.setState(3);// 置为已过期
			couponAwardService.update(couponAward);
		}
		
		// 商城赠送电子券   
		List<CouponGift> list_gift = couponGiftService.findList(null, filters, null);
		for(CouponGift couponGift : list_gift){
			couponGift.setState(3);// 置为已过期
			couponGiftService.update(couponGift);
		}
		
		// 线路赠送电子券   
		List<CouponLine> list_line = null;
		// 1.修改过期的状态
		filters.clear();
		filters.add(new Filter("expireTime", Operator.le, date));
		filters.add(Filter.lt("state", 3)); // 查找冻结(2) 0(未绑定) 1(已绑定) 过期的
		list_line = couponLineService.findList(null, filters, null);
		for(CouponLine couponLine : list_line){
			couponLine.setState(3);// 置为已过期
			couponLineService.update(couponLine);
		}
		
		// 2.解冻电子券
		filters.clear();
		filters.add(new Filter("thawingTime", Operator.le, date));
		filters.add(Filter.eq("state", 2)); 
		list_line = couponLineService.findList(null, filters, null);
		for(CouponLine couponLine : list_line){
			couponLine.setState(0);// 解冻 置为未绑定
			couponLineService.update(couponLine);
		}
		
	}
	
}
