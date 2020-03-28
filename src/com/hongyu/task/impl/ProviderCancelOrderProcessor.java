package com.hongyu.task.impl;

import java.math.BigDecimal;
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

@Component("providerCancelOrderProcessor")
public class ProviderCancelOrderProcessor implements Processor{

	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "hyTicketInboundServiceImpl")
	HyTicketInboundService hyTicketInboundService;
	
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.HOUR, -2);
		Date start = calendar.getTime();

		
		
		List<Filter> filters = new ArrayList<>();
		//支付时间是一小时以前的
		filters.add(Filter.le("payTime", start));
		//支付状态是未支付的
		filters.add(Filter.eq("paystatus", 1));
		//订单状态为待供应商确认
		filters.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM));
		//订单状态为不是全部已退款
		filters.add(Filter.ne("refundstatus", 2));

		List<HyOrder> hyOrders = hyOrderService.findList(null,filters,null);
		for(HyOrder hyOrder:hyOrders){
			try {
				BigDecimal orderMoney = hyOrder.getJiusuanMoney().add(hyOrder.getTip()).subtract(hyOrder.getDiscountedPrice()).subtract(hyOrder.getStoreFanLi())
						.subtract(hyOrder.getJiesuanTuikuan()).subtract(hyOrder.getBaoxianWaimaiTuikuan());
				hyOrderService.cancelOrderAfterPay(hyOrder.getId(),orderMoney);
				
				HyOrderApplication application = new HyOrderApplication();
				application.setContent("供应商确认超时取消订单");
				application.setOperator(null);
				application.setOrderId(hyOrder.getId());
				application.setCreatetime(new Date());
				application.setStatus(HyOrderApplication.STATUS_ACCEPT);
				application.setType(HyOrderApplication.PROVIDER_CONFIRM_OVERTIME_CANCEL_ORDER);
				
				hyOrderApplicationService.save(application);
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}		
		
	}
	

}
