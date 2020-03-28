package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponBalanceUseDao;
import com.hongyu.entity.CouponBalanceUse;
import com.hongyu.service.CouponBalanceUseService;

@Service("couponBalanceUseServiceImpl")
public class CouponBalanceUseServiceImpl extends BaseServiceImpl<CouponBalanceUse, Long>
		implements CouponBalanceUseService {
	@Resource(name = "couponBalanceUseDaoImpl")
	CouponBalanceUseDao dao;

	@Resource(name = "couponBalanceUseDaoImpl")
	public void setBaseDao(CouponBalanceUseDao dao) {
		super.setBaseDao(dao);
	}
}