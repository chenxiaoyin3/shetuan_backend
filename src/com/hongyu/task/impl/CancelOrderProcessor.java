package com.hongyu.task.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.task.Processor;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;

@Component("cancelOrderProcessor")
public class CancelOrderProcessor implements Processor{

	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "hyTicketInboundServiceImpl")
	HyTicketInboundService hyTicketInboundService;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.HOUR, -2);
		Date start = calendar.getTime();

		
		
		List<Filter> filters = new ArrayList<>();
		//创建时间是两小时以前的
		filters.add(Filter.le("createtime", start));

		//支付状态是未支付的
		filters.add(Filter.eq("paystatus", 0));
		//订单状态不是已取消
		filters.add(Filter.ne("status", Constants.HY_ORDER_STATUS_CANCELED));
		List<HyOrder> hyOrders = hyOrderService.findList(null,filters,null);
		for(HyOrder hyOrder:hyOrders){
			try {
				hyOrderService.cancelOrder(hyOrder.getId());
				
				HyOrderApplication application = new HyOrderApplication();
				application.setContent("支付超时取消订单");
				application.setOperator(null);
				application.setOrderId(hyOrder.getId());
				application.setCreatetime(new Date());
				application.setStatus(HyOrderApplication.STATUS_ACCEPT);
				application.setType(HyOrderApplication.PAY_OVERTIME_CANCEL_ORDER);
				
				hyOrderApplicationService.save(application);
				
				Integer type=hyOrder.getType();
				//如果是票务订单,恢复原库存
				if(type==3||type==4||type==5) {
					//恢复库存
					hyTicketInboundService.recoverTicketInboundByTicketOrder(hyOrder);
					
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		
	}

}
