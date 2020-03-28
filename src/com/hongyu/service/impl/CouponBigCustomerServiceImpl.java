package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponBigCustomerDao;
import com.hongyu.entity.CouponBigCustomer;
import com.hongyu.service.CouponBigCustomerService;

@Service("couponBigCustomerServiceImpl")
public class CouponBigCustomerServiceImpl extends BaseServiceImpl<CouponBigCustomer, Long>
		implements CouponBigCustomerService {
	@Resource(name = "couponBigCustomerDaoImpl")
	CouponBigCustomerDao dao;

	@Resource(name = "couponBigCustomerDaoImpl")
	public void setBaseDao(CouponBigCustomerDao dao) {
		super.setBaseDao(dao);
	}
}