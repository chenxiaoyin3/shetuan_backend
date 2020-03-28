package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponSaleDao;
import com.hongyu.entity.CouponSale;
import com.hongyu.service.CouponSaleService;

@Service("couponSaleServiceImpl")
public class CouponSaleServiceImpl extends BaseServiceImpl<CouponSale, Long> implements CouponSaleService {
	@Resource(name = "couponSaleDaoImpl")
	CouponSaleDao dao;

	@Resource(name = "couponSaleDaoImpl")
	public void setBaseDao(CouponSaleDao dao) {
		super.setBaseDao(dao);
	}
}