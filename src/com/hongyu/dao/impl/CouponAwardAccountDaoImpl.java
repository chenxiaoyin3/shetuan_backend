package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponAwardAccountDao;
import com.hongyu.entity.CouponAwardAccount;

@Repository("couponAwardAccountDaoImpl")
public class CouponAwardAccountDaoImpl extends BaseDaoImpl<CouponAwardAccount, Long> implements CouponAwardAccountDao {
}