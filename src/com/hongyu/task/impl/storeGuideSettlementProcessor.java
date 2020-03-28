package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.GuideSettlementDetail;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.Store;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideSettlementDetailService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.StoreService;
import com.hongyu.task.Processor;


@Component("storeGuideSettlementProcessor")
public class storeGuideSettlementProcessor implements Processor{
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "guideSettlementDetailServiceImpl")
	GuideSettlementDetailService guideSettlementDetailService;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		System.out.println("生成租界导游结详情");
		
		try {
			Date today = new Date();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String stoday = sDateFormat.format(today);
			String ceshi = "2018-08-08";
			Date date = sDateFormat.parse(stoday);
			Date dceshi = sDateFormat.parse(ceshi);
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("status", 1));   //已确认
			filters.add(Filter.eq("endDate", date));   //结束日期

			List<GuideAssignment> guideAssignments = guideAssignmentService.findList(null,filters,null);
			
			for(GuideAssignment guideAssignment:guideAssignments){
				
				GuideSettlementDetail guideSettlementDetail = new GuideSettlementDetail();
				guideSettlementDetail.setOrderId(guideAssignment.getOrderId());
				guideSettlementDetail.setPaiqianId(guideAssignment.getId());
				guideSettlementDetail.setServiceFee(guideAssignment.getServiceFee());
				guideSettlementDetail.setAccountPayable(guideAssignment.getTotalFee());
				guideSettlementDetail.setGuiderId(guideAssignment.getGuideId());
				guideSettlementDetail.setGroupId(guideAssignment.getGroupId());
				guideSettlementDetail.setDispatchType(guideAssignment.getAssignmentType());
				guideSettlementDetail.setServiceType(guideAssignment.getServiceType());
				guideSettlementDetail.setStartDate(guideAssignment.getStartDate());
				guideSettlementDetail.setLine(guideAssignment.getLineName());
				guideSettlementDetail.setDays(guideAssignment.getDays());
				guideSettlementDetail.setTip(guideAssignment.getTip());
				guideSettlementDetail.setDeductFee(new BigDecimal(0));
				guideSettlementDetail.setIsCanSettle(true);
				guideSettlementDetail.setStatus(0);
				
				Long orderId = guideAssignment.getOrderId();
				if(orderId!=null){
					HyOrder hyOrder = hyOrderService.find(orderId);
					guideSettlementDetail.setRentStoreId(hyOrder.getStoreId());
					
					List<Filter> filters2 = new ArrayList<>();
					filters2.add(Filter.eq("orderId", orderId));
					Store store = storeService.find(hyOrder.getStoreId());
					guideSettlementDetail.setRentStore(store.getStoreName());
					
					if(hyOrder.getType()==0){
						hyOrder.setStatus(9);
						hyOrderService.update(hyOrder);
					}
					
				}
				
				
				guideSettlementDetailService.save(guideSettlementDetail);	
				System.out.println("生成租界导游结详情");
			}
			System.out.println("生成租界导游结详情成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("生成租界导游结详情失败");
		}
		
		
	}

}
