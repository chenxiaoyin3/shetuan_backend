package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponAwardAccountDao;
import com.hongyu.entity.CouponAwardAccount;
import com.hongyu.service.CouponAwardAccountService;

@Service("couponAwardAccountServiceImpl")
public class CouponAwardAccountServiceImpl extends BaseServiceImpl<CouponAwardAccount, Long>
		implements CouponAwardAccountService {
	@Resource(name = "couponAwardAccountDaoImpl")
	CouponAwardAccountDao dao;

	@Resource(name = "couponAwardAccountDaoImpl")
	public void setBaseDao(CouponAwardAccountDao dao) {
		super.setBaseDao(dao);
	}
}