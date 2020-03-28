package com.hongyu.service;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.StoreRecharge;

public interface StoreRechargeService extends BaseService<StoreRecharge,Long>{
	public Json add(StoreRecharge storeRecharge,HttpSession session);
}
