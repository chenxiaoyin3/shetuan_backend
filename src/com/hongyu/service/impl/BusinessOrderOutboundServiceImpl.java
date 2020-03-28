package com.hongyu.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.dao.BusinessOrderOutboundDao;
import com.hongyu.dao.HyAdminDao;
import com.hongyu.entity.BusinessOrderOutbound;
import com.hongyu.service.BusinessOrderOutboundService;
@Service("businessOrderOutboundServiceImpl")
public class BusinessOrderOutboundServiceImpl extends BaseServiceImpl<BusinessOrderOutbound, Long>
		implements BusinessOrderOutboundService {
	@Resource(name = "businessOrderOutboundDaoImpl")
	BusinessOrderOutboundDao dao;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name = "businessOrderOutboundDaoImpl")
	public void setBaseDao(BusinessOrderOutboundDao dao){
		super.setBaseDao(dao);		
	}
	
	@Override
	public void save(BusinessOrderOutbound businessOrderOutbound)  {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			if (username != null)
				businessOrderOutbound.setOperator(adminDao.find(username));
		}
		super.save(businessOrderOutbound);
	}
}
