package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponLineDao;
import com.hongyu.entity.CouponLine;

@Repository("couponLineDaoImpl")
public class CouponLineDaoImpl extends BaseDaoImpl<CouponLine, Long> implements CouponLineDao {
}