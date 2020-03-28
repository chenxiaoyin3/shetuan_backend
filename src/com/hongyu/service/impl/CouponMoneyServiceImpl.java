package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponMoneyDao;
import com.hongyu.entity.CouponMoney;
import com.hongyu.service.CouponMoneyService;

@Service("couponMoneyServiceImpl")
public class CouponMoneyServiceImpl extends BaseServiceImpl<CouponMoney, Long> implements CouponMoneyService {
	@Resource(name = "couponMoneyDaoImpl")
	CouponMoneyDao dao;

	@Resource(name = "couponMoneyDaoImpl")
	public void setBaseDao(CouponMoneyDao dao) {
		super.setBaseDao(dao);
	}
}