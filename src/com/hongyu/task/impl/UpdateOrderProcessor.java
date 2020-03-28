package com.hongyu.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.TwiceConsumeRecord;
import com.hongyu.entity.TwiceConsumeStatis;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.service.TwiceConsumeRecordService;
import com.hongyu.service.TwiceConsumeStatisService;
import com.hongyu.task.Processor;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;

@Component("updateOrderProcessor")
public class UpdateOrderProcessor implements Processor {
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderServiceImpl;
	
	@Resource(name = "twiceConsumeRecordServiceImpl")
	TwiceConsumeRecordService twiceConsumeRecordService;
	
	@Resource(name = "twiceConsumeStatisServiceImpl")
	TwiceConsumeStatisService twiceConsumeStatisService;
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationSrv;


	@Override
	@Transactional
	public void process() {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("orderState", Constants.BUSINESS_ORDER_STATUS_HAS_RECEIVED));
		Date current = new Date();
		//7天自动设置订单为完成
		Date shouldReceivedDate = DateUtil.getDateAfterSpecifiedDays(current, -7);
		filters.add(Filter.le("receiveTime", shouldReceivedDate));
		
		List<BusinessOrder> orders = businessOrderServiceImpl.findList(null, filters, null);
		
		filters.clear();
		//找10天前发货的
		filters.add(Filter.eq("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE));
		Date shouldShipDate = DateUtil.getDateAfterSpecifiedDays(current, -10);
		filters.add(Filter.le("deliveryTime", shouldShipDate));
		List<BusinessOrder> waitForReceivedOrders = businessOrderServiceImpl.findList(null, filters, null);
		if (waitForReceivedOrders.size() > 0) {
			orders.addAll(waitForReceivedOrders);
		}
		//遍历订单
		for (BusinessOrder order : orders) {
			for (BusinessOrderItem item : order.getBusinessOrderItems()) {
				//修改已售
				if (item.getType() == 0) {
					if (item.getSpecialtySpecification() != null) {
						SpecialtySpecification spec = specialtySpecificationSrv.find(item.getSpecialtySpecification());
						if (spec != null) {
							spec.setHasSold(spec.getHasSold()+item.getQuantity()-item.getReturnQuantity());
							specialtySpecificationSrv.save(spec);
						}
					}
				}
				
			}
			order.setCompleteTime(new Date());
			order.setOrderState(Constants.BUSINESS_ORDER_STATUS_FINISH);
			businessOrderServiceImpl.update(order);
			
			//增加二次消费统计

			Filter filter = new Filter("wechat_id", Operator.eq, order.getWechatAccount().getId());
			long count = twiceConsumeRecordService.count(filter);
			if (count == 0) { //二次消费记录表中没有该用户 
				if (order.getCouponMoney().doubleValue() > 0) { // 订单必须使用电子券
					TwiceConsumeRecord twiceConsumeRecord = new TwiceConsumeRecord();
					twiceConsumeRecord.setWechat_id(order.getWechatAccount().getId());
					twiceConsumeRecord.setConsumer(order.getWechatAccount().getWechatName());
					twiceConsumeRecord.setPhone(order.getOrderPhone());
					twiceConsumeRecord.setOrderCode(order.getOrderCode());
					twiceConsumeRecord.setPayment(order.getTotalMoney().floatValue());
					twiceConsumeRecord.setCouponAmount(order.getCouponMoney().floatValue());
					twiceConsumeRecord.setWechatBalanceAmount(order.getBalanceMoney().floatValue());
					twiceConsumeRecord.setCashAmount(order.getPayMoney().floatValue());
					twiceConsumeRecord.setConsumeTime(order.getOrderTime());
					twiceConsumeRecord.setState(Constants.DUMMY_TWICE_CONSUME); //该次订单之后全款订单视为二次消费
					
					twiceConsumeRecordService.save(twiceConsumeRecord);
				} 
			}
			else { //二次消费记录表中有该用户
				if(order.getPayMoney() == order.getShouldPayMoney()){ // 是有效的二次消费订单(全款)
					//1.修改二次消费记录表
					TwiceConsumeRecord twiceConsumeRecord = new TwiceConsumeRecord();
					twiceConsumeRecord.setWechat_id(order.getWechatAccount().getId());
					twiceConsumeRecord.setConsumer(order.getWechatAccount().getWechatName());
					twiceConsumeRecord.setPhone(order.getOrderPhone());
					twiceConsumeRecord.setOrderCode(order.getOrderCode());
					twiceConsumeRecord.setPayment(order.getTotalMoney().floatValue());
					twiceConsumeRecord.setCouponAmount(order.getCouponMoney().floatValue());
					twiceConsumeRecord.setWechatBalanceAmount(order.getBalanceMoney().floatValue());
					twiceConsumeRecord.setCashAmount(order.getPayMoney().floatValue());
					twiceConsumeRecord.setConsumeTime(order.getOrderTime());
					twiceConsumeRecord.setState(Constants.REAL_TWICE_CONSUME); //该订单视为二次消费
					
					twiceConsumeRecordService.save(twiceConsumeRecord);
					
					
					//2.修改二次消费统计表
					List<Filter> filters3 = new ArrayList<>();
					filters3.add(new Filter("wechatId",Operator.eq,order.getWechatAccount().getId()));
					List<TwiceConsumeStatis> list = twiceConsumeStatisService.findList(null, filters3, null);
					if(list == null || list.size() == 0){ //二次消费统计表中没有该用户  则新建
						TwiceConsumeStatis twiceConsumeStatis = new TwiceConsumeStatis();
						twiceConsumeStatis.setConsumer(order.getWechatAccount().getWechatName());
						twiceConsumeStatis.setPhone(order.getOrderPhone());
						twiceConsumeStatis.setConsumeCount(1); //初始化消费次数
						twiceConsumeStatis.setTotalAmount(order.getTotalMoney().floatValue());
						twiceConsumeStatis.setWechatId(order.getWechatAccount().getId());
						twiceConsumeStatisService.save(twiceConsumeStatis);
					}else{//二次消费统计表中已经有该用户
						TwiceConsumeStatis twiceConsumeStatis = list.get(0);
						twiceConsumeStatis.setConsumeCount(twiceConsumeStatis.getConsumeCount() + 1);
						twiceConsumeStatis.setTotalAmount(twiceConsumeStatis.getTotalAmount() + order.getTotalMoney().floatValue());
						twiceConsumeStatisService.update(twiceConsumeStatis);  //更新二次消费统计表
					}
				}
			}
		}
		
	}

}
