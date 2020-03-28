package com.hongyu.service;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.HyOrder;

public interface HyOrderService extends BaseService<HyOrder, Long> {
	public Json addGuideOrder(HyOrder hyOrder, HttpSession session) throws Exception;

	public Json addLineOrder(Long placeHolder,HyOrder hyOrder, HttpSession session) throws Exception;

	public Json addStoreOrderPayment(Long id, HttpSession session) throws Exception;
	
	public void addStoreOrderBankPayment(String orderNum,String transAmt) throws Exception;
	
	public Json addInsuranceOrderPayment(Long id, HttpSession session) throws Exception;
	
	public BigDecimal getLineRefundPercentage(HyOrder order);
	
	public BigDecimal getTicketRefundPercentage(HyOrder order) throws Exception;
	
	public List<HyOrder> getOrdersByProviderName(String providerName,Integer orderType);
	
	public List<Long> getOrderIdsByProviderName(String providerName,Integer orderType); 
	
	public Boolean cancelOrder(Long id) throws Exception;
	
	public Boolean cancelOrderAfterPay(Long id,BigDecimal orderMoney)throws Exception;
	
	public Boolean cancelInsuranceOrder(Long id) throws Exception;

	public Json addInsuranceOrder(HyOrder hyOrder, HttpSession session) throws Exception;
	
	public Json addVisaOrder(HyOrder hyOrder,HttpSession session) throws Exception;
	
	public Boolean cancelVisaOrder(Long id) throws Exception;
	public Json providerConfirm(Long id, String view, Integer status, HttpSession session);
}
