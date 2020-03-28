package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponAwardDao;
import com.hongyu.entity.CouponAward;
import com.hongyu.service.CouponAwardService;

@Service("couponAwardServiceImpl")
public class CouponAwardServiceImpl extends BaseServiceImpl<CouponAward, Long> implements CouponAwardService {
	@Resource(name = "couponAwardDaoImpl")
	CouponAwardDao dao;

	@Resource(name = "couponAwardDaoImpl")
	public void setBaseDao(CouponAwardDao dao) {
		super.setBaseDao(dao);
	}
}