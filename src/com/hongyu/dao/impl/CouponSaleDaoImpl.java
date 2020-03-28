package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponSaleDao;
import com.hongyu.entity.CouponSale;

@Repository("couponSaleDaoImpl")
public class CouponSaleDaoImpl extends BaseDaoImpl<CouponSale, Long> implements CouponSaleDao {
}