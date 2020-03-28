package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyPromotionPicDao;
import com.hongyu.entity.HyPromotionPic;
import com.hongyu.service.HyPromotionPicService;
@Service(value = "hyPromotionPicServiceImpl")
public class HyPromotionPicServiceImpl extends BaseServiceImpl<HyPromotionPic, Long>
		implements HyPromotionPicService {
	@Resource(name = "hyPromotionPicDaoImpl")
	HyPromotionPicDao dao;
	
	@Resource(name = "hyPromotionPicDaoImpl")
	public void setBaseDao(HyPromotionPicDao dao){
		super.setBaseDao(dao);		
	}

}
