package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponBigCustomerAccountDao;
import com.hongyu.entity.CouponBigCustomerAccount;
import com.hongyu.service.CouponBigCustomerAccountService;

@Service("couponBigCustomerAccountServiceImpl")
public class CouponBigCustomerAccountServiceImpl extends BaseServiceImpl<CouponBigCustomerAccount, Long>
		implements CouponBigCustomerAccountService {
	@Resource(name = "couponBigCustomerAccountDaoImpl")
	CouponBigCustomerAccountDao dao;

	@Resource(name = "couponBigCustomerAccountDaoImpl")
	public void setBaseDao(CouponBigCustomerAccountDao dao) {
		super.setBaseDao(dao);
	}
}