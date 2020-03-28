package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CouponRebateRatioDao;
import com.hongyu.entity.CouponRebateRatio;

@Repository("couponRebateRatioDaoImpl")
public class CouponRebateRatioDaoImpl extends BaseDaoImpl<CouponRebateRatio, Long>
implements CouponRebateRatioDao{

}
