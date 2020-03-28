package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponMoneyHistoryDao;
import com.hongyu.entity.CouponMoneyHistory;
import com.hongyu.service.CouponMoneyHistoryService;

@Service("couponMoneyHistoryServiceImpl")
public class CouponMoneyHistoryServiceImpl extends BaseServiceImpl<CouponMoneyHistory, Long>
		implements CouponMoneyHistoryService {
	@Resource(name = "couponMoneyHistoryDaoImpl")
	CouponMoneyHistoryDao dao;

	@Resource(name = "couponMoneyHistoryDaoImpl")
	public void setBaseDao(CouponMoneyHistoryDao dao) {
		super.setBaseDao(dao);
	}
}