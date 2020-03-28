package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponBalanceUseDao;
import com.hongyu.entity.CouponBalanceUse;

@Repository("couponBalanceUseDaoImpl")
public class CouponBalanceUseDaoImpl extends BaseDaoImpl<CouponBalanceUse, Long> implements CouponBalanceUseDao {
}