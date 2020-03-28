package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponDao;
import com.hongyu.entity.Coupon;
import com.hongyu.service.CouponService;

@Service("couponServiceImpl")
public class CouponServiceImpl extends BaseServiceImpl<Coupon, Long> implements CouponService {
	@Resource(name = "couponDaoImpl")
	CouponDao dao;

	@Resource(name = "couponDaoImpl")
	public void setBaseDao(CouponDao dao) {
		super.setBaseDao(dao);
	}
}