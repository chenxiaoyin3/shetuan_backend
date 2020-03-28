package com.hongyu.service;

import java.math.BigDecimal;

import com.grain.service.BaseService;

import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyPromotionActivity;
import com.hongyu.entity.HyOrder;

public interface HyPromotionActivityService extends BaseService<HyPromotionActivity, Long> {
	
	public Page<HyPromotionActivity> findAuditPage(HyAdmin auditor, Pageable pageable, HyPromotionActivity query);
	BigDecimal getDiscountedPriceByHyOrder(HyOrder hyOrder,HyPromotionActivity promotionActivity);
}
