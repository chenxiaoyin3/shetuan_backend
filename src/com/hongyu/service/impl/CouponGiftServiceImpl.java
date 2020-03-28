package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponGiftDao;
import com.hongyu.entity.CouponGift;
import com.hongyu.service.CouponGiftService;

@Service("couponGiftServiceImpl")
public class CouponGiftServiceImpl extends BaseServiceImpl<CouponGift, Long> implements CouponGiftService {
	@Resource(name = "couponGiftDaoImpl")
	CouponGiftDao dao;

	@Resource(name = "couponGiftDaoImpl")
	public void setBaseDao(CouponGiftDao dao) {
		super.setBaseDao(dao);
	}
}