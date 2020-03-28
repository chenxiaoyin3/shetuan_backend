package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponLineDao;
import com.hongyu.entity.CouponLine;
import com.hongyu.service.CouponLineService;

@Service("couponLineServiceImpl")
public class CouponLineServiceImpl extends BaseServiceImpl<CouponLine, Long> implements CouponLineService {
	@Resource(name = "couponLineDaoImpl")
	CouponLineDao dao;

	@Resource(name = "couponLineDaoImpl")
	public void setBaseDao(CouponLineDao dao) {
		super.setBaseDao(dao);
	}
}