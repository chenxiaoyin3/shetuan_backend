package com.hongyu.task.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.HyOrder;
import com.hongyu.service.HyOrderService;
import com.hongyu.task.Processor;
import com.hongyu.util.DateUtil;

/**
 * 定时扫描租借导游订单，如果已回团，将状态改为待评价
 * @author wayne
 *
 */
@Component("guideAssignmentViewProcessor")
public class GuideAssignmentViewProcessor implements Processor{

	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		//获取状态为导游通过的所有租借导游订单
		List<Filter> orderFilters = new ArrayList<>();
		
		orderFilters.add(Filter.eq("type", 0));	//租借导游订单
		orderFilters.add(Filter.eq("status", 3));	//导游通过
		
		List<HyOrder> hyOrders = hyOrderService.findList(null,orderFilters,null);
		if(hyOrders==null || hyOrders.isEmpty()) {
			return;
		}
		

		Date nowadate = new Date();
		Date predate = DateUtil.getPreDay(nowadate);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String preStr = dateFormat.format(predate);
		for(HyOrder hyOrder:hyOrders) {
			Date fatuandate = hyOrder.getFatuandate();
			Integer tianshu = hyOrder.getTianshu();
			Date huituandate = DateUtil.getDateAfterSpecifiedDays(fatuandate, tianshu);
			String huituanStr = dateFormat.format(huituandate);
			if(huituanStr.equals(preStr)) {
				hyOrder.setStatus(9);	//待评价
				hyOrderService.update(hyOrder);
			}
		}
		
		
	}
	

}
