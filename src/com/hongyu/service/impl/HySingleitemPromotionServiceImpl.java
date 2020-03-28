package com.hongyu.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.dao.HySingleitemPromotionDao;
import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.HySingleitemPromotion;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.HyPromotion.PromotionStatus;
import com.hongyu.service.HyPromotionService;
import com.hongyu.service.HySingleitemPromotionService;
import com.hongyu.service.SpecialtySpecificationService;
@Service(value = "hySingleitemPromotionServiceImpl")
public class HySingleitemPromotionServiceImpl extends BaseServiceImpl<HySingleitemPromotion, Long>
		implements HySingleitemPromotionService {
	@Resource(name = "hySingleitemPromotionDaoImpl")
	HySingleitemPromotionDao dao;
	
	@Resource(name = "hySingleitemPromotionDaoImpl")
	public void setBaseDao(HySingleitemPromotionDao dao){
		super.setBaseDao(dao);		
	}
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;
	
	@Resource(name = "hyPromotionServiceImpl")
	HyPromotionService hyPromotionService;
	
	/* 获取有效普通优惠明细 */
	@Override
	public HySingleitemPromotion getValidSingleitemPromotion(Long specialtySpecificationId,Long promotionId) {
		if(specialtySpecificationId == null || promotionId == null) {
			return null;
		}
		SpecialtySpecification specialtySpecification = specialtySpecificationService.find(specialtySpecificationId);
		if (specialtySpecification == null) {
			return null;
		}
		HyPromotion promotion = hyPromotionService.find(promotionId);
		if(promotion == null) {
			return null;
		}
		
		List<Filter> singleFilters = new ArrayList<>();
		singleFilters.add(new Filter("specificationId", Filter.Operator.eq, specialtySpecification));
		singleFilters.add(Filter.eq("hyPromotion", promotion));
		List<Order> singleOrders = new ArrayList<>();
		singleOrders.add(Order.desc("id"));
		List<HySingleitemPromotion> singleitemPromotions = this.findList(null,
				singleFilters, singleOrders);
		if (singleitemPromotions == null || singleitemPromotions.isEmpty()) {
			return null;
		}

		return singleitemPromotions.get(0);
	}
	
	/* 获取有效普通优惠明细 */
	@Override
	public HySingleitemPromotion getValidSingleitemPromotion(Long specialtySpecificationId) {
		if(specialtySpecificationId == null) {
			return null;
		}
		SpecialtySpecification specialtySpecification = specialtySpecificationService.find(specialtySpecificationId);
		List<Filter> singleFilters = new ArrayList<>();
		singleFilters.add(new Filter("specificationId", Filter.Operator.eq, specialtySpecification));
		List<Order> singleOrders = new ArrayList<>();
		singleOrders.add(Order.desc("id"));
		List<HySingleitemPromotion> singleitemPromotions = this.findList(null,
				singleFilters, singleOrders);
		if (singleitemPromotions == null || singleitemPromotions.isEmpty()) {
			return null;
		}
		for (HySingleitemPromotion singleitemPromotion : singleitemPromotions) {
			if (singleitemPromotion.getHyPromotion().getStatus() == PromotionStatus.进行中) {
				return singleitemPromotion;
			}
		}
		return null;
	}



}
