package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hongyu.Filter;
import com.hongyu.entity.OrderItemDivide;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WeDivideReport;
import com.hongyu.service.OrderItemDivideService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.service.WeDivideReportService;
import com.hongyu.task.Processor;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;

@Component("weBusinessDivideDailyReportProcessor")
public class WeBusinessDivideDailyReportProcessor implements Processor {
	
	@Resource(name="orderItemDivideServiceImpl")
	OrderItemDivideService orderItemDivideService;
	
	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;
	
	@Resource(name = "weDivideReportServiceImpl")
	WeDivideReportService weDivideReportServiceImpl;
	
	@Override
	@Transactional
	public void process() {
		// TODO Auto-generated method stub
//		List<Filter> filters = new ArrayList<Filter>();
		//为什么用前一天的时间，这样能保证前一天所有记录被统计出来，万一以后有用户自己确认订单的功能也能保证统计
		Date yesterday = DateUtil.getPreDay(new Date());
		Date begin = DateUtil.getStartOfDay(yesterday);
		Date end = DateUtil.getEndOfDay(yesterday);
		
		HashMap<String, Object> hm = new HashMap<String, Object>();
		List<HashMap<String, Object>> result = new ArrayList<>();
		
		List<WeBusiness> weBusinesses = weBusinessService.findAll();				
		for (WeBusiness tmp : weBusinesses) {
			Boolean flag = false;
			BigDecimal divideTotal= new BigDecimal(0.00);
			BigDecimal salesTotal = new BigDecimal(0.00);
			
			List<Filter> filters2=new ArrayList<>();
			filters2.add(Filter.eq("weBusiness", tmp));
			filters2.add(Filter.ge("acceptTime", begin));
			filters2.add(Filter.le("acceptTime", end));
			List<OrderItemDivide> list1=orderItemDivideService.findList(null, filters2, null);
			if(list1.size()>0) {
				flag = true;
				
				for(OrderItemDivide orderItem:list1){
					divideTotal=divideTotal.add(orderItem.getWeBusinessAmount());
					salesTotal=salesTotal.add(orderItem.getTotalAmount());
				}
			}
			
			List<Filter> filters3=new ArrayList<>();
			filters3.add(Filter.eq("rWeBusiness", tmp));
			filters3.add(Filter.ge("acceptTime", begin));
			filters3.add(Filter.le("acceptTime", end));
			List<OrderItemDivide> list2=orderItemDivideService.findList(null, filters3, null);
			
			if(list2.size()>0) {
				flag = true;
				
				for(OrderItemDivide orderItem:list2){
					divideTotal=divideTotal.add(orderItem.getrWeBusinessAmount());
					salesTotal=salesTotal.add(orderItem.getTotalAmount());
				}
			}
			
			List<Filter> filters4=new ArrayList<>();
			filters4.add(Filter.eq("mWeBusiness", tmp));
			filters4.add(Filter.ge("acceptTime", begin));
			filters4.add(Filter.le("acceptTime", end));
			List<OrderItemDivide> list3=orderItemDivideService.findList(null, filters4, null);
			
			if(list3.size()>0) {
				flag = true;
				
				for(OrderItemDivide orderItem:list3){
					divideTotal=divideTotal.add(orderItem.getmWeBusinessAmount());
					salesTotal=salesTotal.add(orderItem.getTotalAmount());
				}
			}
			
			if (flag) {
				WeDivideReport report = new WeDivideReport();
				report.setWeBusiness(tmp);
				report.setDivideAmount(divideTotal);
				report.setSalesAmount(salesTotal);
				report.setSalesTime(yesterday);
				weDivideReportServiceImpl.save(report);
			}
		}
	}

}
