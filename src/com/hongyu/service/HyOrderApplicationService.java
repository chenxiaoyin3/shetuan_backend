package com.hongyu.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;

public interface HyOrderApplicationService extends BaseService<HyOrderApplication, Long> {
	public void handleStoreTuiTuan(HyOrderApplication application)throws Exception;
	public void handleStoreShouHou(HyOrderApplication application)throws Exception;
	public Map<String, Object> auditDetailHelper(HyOrderApplication application,Integer status)throws Exception;
	public List<Map<String, Object>> auditItemsHelper(HyOrderApplication application)throws Exception;
	public void handleBaoXianTuiKuan(HyOrderApplication application, boolean isPartRefund) throws Exception;
	public Json getApplicationList(Pageable pageable, Integer status, String providerName, HttpSession session,
			Integer type,Integer orderType);
	
	public void handleTicketHotelScg(HyOrderApplication application)throws Exception;

	public void handleTicketHotelScs(HyOrderApplication application)throws Exception;
	
	public void handleTicketHotelandsceneScg(HyOrderApplication application)throws Exception;

	public void handleTicketHotelandsceneScs(HyOrderApplication application)throws Exception;
	
	public void handleTicketSceneScg(HyOrderApplication application)throws Exception;

	public void handleTicketSceneScs(HyOrderApplication application)throws Exception;
	
	//签证订单售前退款后处理
	public void handleHyVisaScg(HyOrderApplication application)throws Exception;
	//签证订单售后处理
	public void handleHyVisaScs(HyOrderApplication application)throws Exception;
	public void handleTicketSubscribeScs(HyOrderApplication application) throws Exception;
	
}

