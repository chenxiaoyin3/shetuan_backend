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
import com.hongyu.dao.MenHuBannerDao;
import com.hongyu.entity.BusinessBanner;
import com.hongyu.entity.MenHuBanner;
import com.hongyu.service.BusinessBannerService;
import com.hongyu.service.MenHuBannerService;

@Service("menHuBannerServiceImpl")
public class MenHuBannerServiceImpl 
extends BaseServiceImpl<MenHuBanner, Long> 
implements MenHuBannerService {
	
	@Resource(name="menHuBannerDaoImpl")
	private MenHuBannerDao menHuBannerDaoImpl;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name="menHuBannerDaoImpl")
	public void setBaseDao(MenHuBannerDao dao){
		super.setBaseDao(dao);
	}
	
	@Override
	public void save(MenHuBanner businessBanner)  {
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
