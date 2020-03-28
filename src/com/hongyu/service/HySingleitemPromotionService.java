package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.HySingleitemPromotion;

public interface HySingleitemPromotionService extends BaseService<HySingleitemPromotion, Long> {
	
	HySingleitemPromotion getValidSingleitemPromotion(Long specialtySpecificationId,Long promotionId);

	HySingleitemPromotion getValidSingleitemPromotion(Long specialtySpecificationId);
}
