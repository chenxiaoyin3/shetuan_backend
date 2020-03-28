package com.hongyu.task.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.task.Processor;

/**
 * 扫描保险订单支付状态的定时器 如果超过投保时间依旧是未支付，则自动变为已取消，每1分钟扫描一次
 * @author lbc
 *
 */
@Component("insuranceOrderAutoCancelProcessor")
public class InsuranceOrderAutoCancelProcessor implements Processor {
	
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
			//获得当前时间
			Date date = new Date();
//			Calendar calendar = Calendar.getInstance();
//			calendar.setTime(date);
//			calendar.add(Calendar.HOUR_OF_DAY, -2);
//			date = calendar.getTime();
			
			//扫描整个hyorderApplication表  如果创建时间小于date type为13 14 15 status为0（待财务审核）  则自动驳回
			//如果保险生效时间小于当前时间，自动取消
			List<Filter> filters = new LinkedList<>();
			//自主投保 未投保的
			filters.add(Filter.eq("type", 1));
			filters.add(Filter.eq("status", 1));
			//保险起始日期 小于当前时间
			filters.add(Filter.lt("insuranceStarttime", date));
			
			
			List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null, filters, null);
			//对每一个虹宇的未支付保险订单
			for(InsuranceOrder insuranceOrder: insuranceOrders) {
				//对每个insuranceOrder,状态也变为已取消(status变为2)
				//状态变为已取消
				insuranceOrder.setStatus(2);
				insuranceOrderService.update(insuranceOrder);
				Long orderId = insuranceOrder.getOrderId();
				HyOrder hyOrder = hyOrderService.find(orderId);
				//已取消
				hyOrder.setStatus(6);
				hyOrderService.update(hyOrder);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		
		
	}

}
