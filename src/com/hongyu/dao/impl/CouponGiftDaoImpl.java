package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponGiftDao;
import com.hongyu.entity.CouponGift;

@Repository("couponGiftDaoImpl")
public class CouponGiftDaoImpl extends BaseDaoImpl<CouponGift, Long> implements CouponGiftDao {
}