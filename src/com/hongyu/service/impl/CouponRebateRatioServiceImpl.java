package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponRebateRatioDao;
import com.hongyu.entity.CouponRebateRatio;
import com.hongyu.service.CouponRebateRatioService;

@Service("couponRebateRatioServiceImpl")
public class CouponRebateRatioServiceImpl extends BaseServiceImpl<CouponRebateRatio, Long> implements CouponRebateRatioService{
	@Resource(name="couponRebateRatioDaoImpl")
	CouponRebateRatioDao couponRebateRatioDaoImpl;
	  
	  @Resource(name="couponRebateRatioDaoImpl")
	  public void setBaseDao(CouponRebateRatioDao dao)
	  {
	    super.setBaseDao(dao);
	  }
}
