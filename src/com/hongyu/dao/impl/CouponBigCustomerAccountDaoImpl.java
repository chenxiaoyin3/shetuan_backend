package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponBigCustomerAccountDao;
import com.hongyu.entity.CouponBigCustomerAccount;

@Repository("couponBigCustomerAccountDaoImpl")
public class CouponBigCustomerAccountDaoImpl extends BaseDaoImpl<CouponBigCustomerAccount, Long>
		implements CouponBigCustomerAccountDao {
}