package com.hongyu.task.impl;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.task.Processor;

/**
 * 扫描保险订单退款申请是否超过两小时的定时器，如果超过状态依旧是待审核，则自动驳回，每1分钟扫描一次
 * @author lbc
 *
 */
@Component("insuranceOrderRefundAutoRejectProcessor")
public class InsuranceOrderRefundAutoRejectProcessor implements Processor {
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;

	@Override
	public void process() {
		// TODO Auto-generated method stub
		//拿到当前时间
		
		try {
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.HOUR_OF_DAY, -2);
			date = calendar.getTime();
			
			//扫描整个hyorderApplication表  如果创建时间小于date type为13 14 15 status为0（待财务审核）  则自动驳回
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.le("createtime", date));
			filters.add(Filter.ge("type", 13));
			filters.add(Filter.le("type", 15));
			filters.add(Filter.eq("status", 0));
			
			List<HyOrderApplication> hyOrderApplications = hyOrderApplicationService.findList(null, filters, null);
			for(HyOrderApplication hyOrderApplication : hyOrderApplications) {
				//状态变为已驳回
				hyOrderApplication.setStatus(2);
				Long orderId = hyOrderApplication.getOrderId();
				HyOrder hyOrder = hyOrderService.find(orderId);
				//退款已驳回
				hyOrder.setRefundstatus(4);
				hyOrderApplicationService.update(hyOrderApplication);
				hyOrderService.update(hyOrder);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		
		
	}

}
