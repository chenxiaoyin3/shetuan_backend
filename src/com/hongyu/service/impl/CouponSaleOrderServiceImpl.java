package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponSaleOrderDao;
import com.hongyu.entity.CouponSaleOrder;
import com.hongyu.service.CouponSaleOrderService;

@Service("couponSaleOrderServiceImpl")
public class CouponSaleOrderServiceImpl extends BaseServiceImpl<CouponSaleOrder, Long>
		implements CouponSaleOrderService {
	@Resource(name = "couponSaleOrderDaoImpl")
	CouponSaleOrderDao dao;

	@Resource(name = "couponSaleOrderDaoImpl")
	public void setBaseDao(CouponSaleOrderDao dao) {
		super.setBaseDao(dao);
	}
}