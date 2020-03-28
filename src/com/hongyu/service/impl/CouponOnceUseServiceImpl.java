package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponOnceUseDao;
import com.hongyu.entity.CouponOnceUse;
import com.hongyu.service.CouponOnceUseService;

@Service("couponOnceUseServiceImpl")
public class CouponOnceUseServiceImpl extends BaseServiceImpl<CouponOnceUse, Long> implements CouponOnceUseService {
	@Resource(name = "couponOnceUseDaoImpl")
	CouponOnceUseDao dao;

	@Resource(name = "couponOnceUseDaoImpl")
	public void setBaseDao(CouponOnceUseDao dao) {
		super.setBaseDao(dao);
	}
}