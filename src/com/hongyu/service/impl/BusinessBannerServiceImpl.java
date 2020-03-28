package com.hongyu.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.dao.BusinessBannerDao;
import com.hongyu.dao.HyAdminDao;
import com.hongyu.entity.BusinessBanner;
import com.hongyu.service.BusinessBannerService;

@Service("businessBannerServiceImpl")
public class BusinessBannerServiceImpl 
extends BaseServiceImpl<BusinessBanner, Long> 
implements BusinessBannerService {
	
	@Resource(name="businessBannerDaoImpl")
	private BusinessBannerDao businessBannerDaoImpl;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name="businessBannerDaoImpl")
	public void setBaseDao(BusinessBannerDao dao){
		super.setBaseDao(dao);
	}
	
	@Override
	public void save(BusinessBanner businessBanner)  {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			if (username != null)
				businessBanner.setCreator(adminDao.find(username));
		}
		super.save(businessBanner);
	}

}
