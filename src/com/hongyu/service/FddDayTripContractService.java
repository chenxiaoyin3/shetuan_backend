package com.hongyu.service;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.FddDayTripContract;
import com.hongyu.entity.HyOrder;

public interface FddDayTripContractService extends BaseService<FddDayTripContract, Long>{
	/*给门店签章使用*/
	public HashMap<String,Object> getDynamicTables(HyOrder order)  throws Exception;
	public HashMap<String,Object> fillIn(Long orderId,HttpSession session) throws Exception;
	public HashMap<String,Object> submit(FddDayTripContract fddDayTripContract) throws Exception;
	public String extCustomerSign(Long id) throws Exception;
	/*给官网签章使用*/
	public HashMap<String, Object> getMhDynamicTables(HyOrder order) throws Exception;
	public HashMap<String,Object> fillInForMh(Long orderId) throws Exception;
	public HashMap<String,Object> submitForMh(FddDayTripContract fddDayTripContract) throws Exception;
	public Json autoSignContractForMh(HyOrder order) throws Exception;	
}
