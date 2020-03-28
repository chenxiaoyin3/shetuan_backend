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
import com.hongyu.dao.HySupplierElementDao;
import com.hongyu.entity.BusinessBanner;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.service.HySupplierElementService;
@Service(value = "hySupplierElementServiceImpl")
public class HySupplierElementServiceImpl extends BaseServiceImpl<HySupplierElement, Long>
		implements HySupplierElementService {
	@Resource(name = "hySupplierElementDaoImpl")
	HySupplierElementDao dao;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name = "hySupplierElementDaoImpl")
	public void setBaseDao(HySupplierElementDao dao){
		super.setBaseDao(dao);		
	}	
	
	@Override
	public void save(HySupplierElement hySupplierElement)  {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			if (username != null)
				hySupplierElement.setOperator(adminDao.find(username));
		}
		super.save(hySupplierElement);
	}
}
