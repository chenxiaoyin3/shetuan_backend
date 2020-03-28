package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.Vip;
import com.hongyu.entity.Viplevel;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.PointrecordService;
import com.hongyu.service.VipService;
import com.hongyu.service.ViplevelService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.task.Processor;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;

/** 会员-定时扫描-每天上午1点扫描前一天某用户完全用现金支付的订单总额，如果满318元，则设置为会员*/
@Component("vipUserProcessor")
public class VipUserProcessor implements Processor {

	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderService;
	
	@Resource(name="wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@Resource(name = "pointrecordServiceImpl")
	PointrecordService pointrecordService;
	
	@Resource(name = "vipServiceImpl")
	VipService vipService;
	
	@Resource(name = "viplevelServiceImpl")
	ViplevelService viplevelService;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		//获取前一天所有的支付订单
		Date preDate = DateUtil.getPreDay(new Date());
		List<Filter> orderFilters = new ArrayList<>();
		orderFilters.add(Filter.gt("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_PAY));
		orderFilters.add(Filter.ge("payTime", DateUtil.getStartOfDay(preDate)));
		orderFilters.add(Filter.le("payTime", DateUtil.getEndOfDay(preDate)));
		List<BusinessOrder> orders = businessOrderService.findList(null,orderFilters,null);
		if(orders==null || orders.isEmpty()) {
			return;
		}
		Map<WechatAccount, BigDecimal> maps = new HashMap<>();
		for(BusinessOrder businessOrder:orders) {
			//判断订单是否是全部现金支付的未参加优惠活动的订单
			if(!businessOrderService.havePromotions(businessOrder)) {
				//没有参加过优惠活动
				if(businessOrder.getShouldPayMoney().equals(businessOrder.getPayMoney())) {
					//如果本订单的全部用现金支付
					WechatAccount wechatAccount = businessOrder.getWechatAccount();
					if(!maps.containsKey(wechatAccount)) {
						//如果没有包含这个记录
						maps.put(wechatAccount, BigDecimal.ZERO);
					}
					//统计该用户的支付金额
					maps.put(wechatAccount, maps.get(wechatAccount).add(businessOrder.getPayMoney()));
				}
			}
		}
		
		for(Entry<WechatAccount,BigDecimal> entry:maps.entrySet()) {
			WechatAccount wechatAccount = entry.getKey();
			BigDecimal money = entry.getValue();

			//判断是否318会员
			vipService.setVip318(wechatAccount, money);
				
		}
		
		
	}
	

}
