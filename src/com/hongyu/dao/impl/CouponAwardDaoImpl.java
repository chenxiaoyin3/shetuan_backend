package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponAwardDao;
import com.hongyu.entity.CouponAward;

@Repository("couponAwardDaoImpl")
public class CouponAwardDaoImpl extends BaseDaoImpl<CouponAward, Long> implements CouponAwardDao {
}