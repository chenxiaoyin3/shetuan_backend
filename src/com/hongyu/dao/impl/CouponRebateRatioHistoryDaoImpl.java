package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponRebateRatioHistoryDao;
import com.hongyu.entity.CouponRebateRatioHistory;

@Repository("couponRebateRatioHistoryDaoImpl")
public class CouponRebateRatioHistoryDaoImpl extends BaseDaoImpl<CouponRebateRatioHistory, Long> implements CouponRebateRatioHistoryDao {

}
