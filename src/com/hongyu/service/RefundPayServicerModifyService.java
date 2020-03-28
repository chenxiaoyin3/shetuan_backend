package com.hongyu.service;

import com.hongyu.Json;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.Store;

public interface RefundPayServicerModifyService {
	public Json tuituan(HyOrder hyOrder,HyOrderApplication application,HySupplierContract contract,String username,Store store);

	public Json xiaotuan(HyOrder hyOrder,HyOrderApplication application,HySupplierContract contract,String username,Store store);
	
}
