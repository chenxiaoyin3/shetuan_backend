package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupitemPromotionDao;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.service.HyGroupitemPromotionService;
@Service(value = "hyGroupitemPromotionServiceImpl")
public class HyGroupitemPromotionServiceImpl extends BaseServiceImpl<HyGroupitemPromotion, Long>
		implements HyGroupitemPromotionService {
	@Resource(name = "hyGroupitemPromotionDaoImpl")
	HyGroupitemPromotionDao dao;
	
	@Resource(name = "hyGroupitemPromotionDaoImpl")
	public void setBaseDao(HyGroupitemPromotionDao dao){
		super.setBaseDao(dao);		
	}

}
