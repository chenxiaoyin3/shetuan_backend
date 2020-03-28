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
import com.hongyu.dao.HyDistributorManagementDao;
import com.hongyu.entity.HyDistributorManagement;
import com.hongyu.service.HyDistributorManagementService;

@Service("hyDistributorManagementServiceImpl")
public class HyDistributorManagementServiceImpl extends BaseServiceImpl<HyDistributorManagement,Long>
        implements HyDistributorManagementService{
	@Resource(name = "hyDistributorManagementDaoImpl")
	HyDistributorManagementDao dao;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name = "hyDistributorManagementDaoImpl")
	public void setBaseDao(HyDistributorManagementDao dao){
		super.setBaseDao(dao);		
	}	
	@Override
	public void save(HyDistributorManagement hyDistributorManagement){
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();	
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			if (username != null)
				hyDistributorManagement.setCreator(adminDao.find(username));
		}
		super.save(hyDistributorManagement);
	}
}
