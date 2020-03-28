package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponSaleOrderDao;
import com.hongyu.entity.CouponSaleOrder;

@Repository("couponSaleOrderDaoImpl")
public class CouponSaleOrderDaoImpl extends BaseDaoImpl<CouponSaleOrder, Long> implements CouponSaleOrderDao {
}