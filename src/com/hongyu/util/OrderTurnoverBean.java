package com.hongyu.util;

import java.util.Map;

public class OrderTurnoverBean {
	private Map<String, Object> maps;
	public OrderTurnoverBean(Map<String, Object> maps){
		this.maps=maps;
	}
	public Object getStaffName(){
		return maps.get("staffName");
	}
	public Object getOrderNum(){
		return maps.get("orderNum");
	}
	public Object getCustomerNum(){
		return maps.get("customerNum");
	}
	public Object getTurnover(){
		return maps.get("turnover");
	}
	public Object getStoreName(){
		return maps.get("storeName");
	}
	public Object getStoreNum(){
		return maps.get("storeNum");
	}
	public Object getBranchName(){
		return maps.get("branchName");
	}
	public Object getProviderName(){
		return maps.get("providerName");
	}
}
