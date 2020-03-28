package com.hongyu.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.dao.HyAdminDao;
import com.hongyu.dao.HyPromotionDao;
import com.hongyu.entity.HyPromotion;
import com.hongyu.service.HyPromotionService;
@Service(value = "hyPromotionServiceImpl")
public class HyPromotionServiceImpl extends BaseServiceImpl<HyPromotion, Long>
		implements HyPromotionService {
	@Resource(name = "hyPromotionDaoImpl")
	HyPromotionDao dao;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name = "hyPromotionDaoImpl")
	public void setBaseDao(HyPromotionDao dao){
		super.setBaseDao(dao);		
	}

	@Override
	public void save(HyPromotion hyPromotion)  {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			if (username != null)
				hyPromotion.setCreator(adminDao.find(username));
		}
		super.save(hyPromotion);
	}
}
