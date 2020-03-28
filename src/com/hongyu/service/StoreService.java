package com.hongyu.service;

import java.math.BigDecimal;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.BankList;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;

public interface StoreService extends BaseService<Store, Long> {

	public Json saveStore(Store store, String storeAddress, String hyAdminAddress, BankList bankList, Long areaId,
			HyAdmin hyAdmin, Long roleId, BigDecimal lineDivideProportion, HttpSession httpSession);
	public Json editStore(Store store, String storeAddress, String hyAdminAddress, Long storeId, Long areaId,
			Long bankId, Long roleId, BankList bankList, HyAdmin hyAdmin);
	public Store findStore(HyAdmin hyAdmin);
}
