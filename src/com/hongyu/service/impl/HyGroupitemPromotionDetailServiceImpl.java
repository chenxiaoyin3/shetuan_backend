package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupitemPromotionDetailDao;
import com.hongyu.entity.HyGroupitemPromotionDetail;
import com.hongyu.service.HyGroupitemPromotionDetailService;
@Service(value = "hyGroupitemPromotionDetailServiceImpl")
public class HyGroupitemPromotionDetailServiceImpl extends BaseServiceImpl<HyGroupitemPromotionDetail, Long>
		implements HyGroupitemPromotionDetailService {
	@Resource(name = "hyGroupitemPromotionDetailDaoImpl")
	HyGroupitemPromotionDetailDao dao;
	
	@Resource(name = "hyGroupitemPromotionDetailDaoImpl")
	public void setBaseDao(HyGroupitemPromotionDetailDao dao){
		super.setBaseDao(dao);		
	}

}
