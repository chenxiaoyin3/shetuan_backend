package com.hongyu.service;


import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.BankList;
import com.hongyu.entity.HyAddedServiceSupplier;
import com.hongyu.entity.HyAdmin;

public interface HyAddedServiceSupplierService extends BaseService<HyAddedServiceSupplier,Long>{

	public Json addSupplier(HyAddedServiceSupplier hyAddedServiceSupplier,HyAdmin hyAdmin);
	public Json editSuppier(HyAddedServiceSupplier hyAddedServiceSupplier);
}
