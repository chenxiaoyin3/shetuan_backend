package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponSaleAccountDao;
import com.hongyu.entity.CouponSaleAccount;

@Repository("couponSaleAccountDaoImpl")
public class CouponSaleAccountDaoImpl extends BaseDaoImpl<CouponSaleAccount, Long> implements CouponSaleAccountDao {
}