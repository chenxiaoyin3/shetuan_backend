package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponSaleAccountDao;
import com.hongyu.entity.CouponSaleAccount;
import com.hongyu.service.CouponSaleAccountService;

@Service("couponSaleAccountServiceImpl")
public class CouponSaleAccountServiceImpl extends BaseServiceImpl<CouponSaleAccount, Long>
		implements CouponSaleAccountService {
	@Resource(name = "couponSaleAccountDaoImpl")
	CouponSaleAccountDao dao;

	@Resource(name = "couponSaleAccountDaoImpl")
	public void setBaseDao(CouponSaleAccountDao dao) {
		super.setBaseDao(dao);
	}
}