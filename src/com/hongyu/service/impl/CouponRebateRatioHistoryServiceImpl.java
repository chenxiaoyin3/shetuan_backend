package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CouponRebateRatioHistoryDao;
import com.hongyu.entity.CouponRebateRatioHistory;
import com.hongyu.service.CouponRebateRatioHistoryService;

@Service("couponRebateRatioHistoryServiceImpl")
public class CouponRebateRatioHistoryServiceImpl extends BaseServiceImpl<CouponRebateRatioHistory, Long> implements CouponRebateRatioHistoryService{
	@Resource(name="couponRebateRatioHistoryDaoImpl")
	CouponRebateRatioHistoryDao couponRebateRatioHistoryDaoImpl;
	  
	  @Resource(name="couponRebateRatioHistoryDaoImpl")
	  public void setBaseDao(CouponRebateRatioHistoryDao dao)
	  {
	    super.setBaseDao(dao);
	  }
}
