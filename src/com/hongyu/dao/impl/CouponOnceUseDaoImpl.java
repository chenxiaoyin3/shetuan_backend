package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponOnceUseDao;
import com.hongyu.entity.CouponOnceUse;

@Repository("couponOnceUseDaoImpl")
public class CouponOnceUseDaoImpl extends BaseDaoImpl<CouponOnceUse, Long> implements CouponOnceUseDao {
}