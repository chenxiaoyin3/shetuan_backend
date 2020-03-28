package com.hongyu.service;



import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.FddContract;
import com.hongyu.entity.FddDayTripContract;
import com.hongyu.entity.HyOrder;
import com.hongyu.util.contract.SelfPayAgreement;
import com.hongyu.util.contract.ShoppingAgreement;

public interface FddContractService extends BaseService<FddContract, Long> {
	/*给门店签章使用*/
	public HashMap<String,Object> getDynamicTables(HyOrder order,List<ShoppingAgreement> shoppingAgreements,List<SelfPayAgreement> selfPayAgreements)  throws Exception;
	public HashMap<String,Object> fillIn(Long orderId,HttpSession session) throws Exception;
	public HashMap<String,Object> submit(FddContract fddContract,List<ShoppingAgreement> shoppingAgreements,List<SelfPayAgreement> selfPayAgreements) throws Exception;
	public String extCustomerSign(Long id) throws Exception;
	/*给官网签章使用*/
	public HashMap<String, Object> getMhDynamicTables(HyOrder order) throws Exception;
	public Json autoSignContractForMh(HyOrder order) throws Exception;
	public HashMap<String,Object> fillInForMh(Long orderId) throws Exception;
	public HashMap<String,Object> submitForMh(FddContract fddContract) throws Exception;
}
