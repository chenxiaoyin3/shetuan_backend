package com.hongyu.service.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Principal;
import com.hongyu.dao.HyAdminDao;
import com.hongyu.dao.HySupplierDao;
import com.hongyu.entity.HySupplier;
import com.hongyu.service.HySupplierService;
@Service(value = "hySupplierServiceImpl")
public class HySupplierServiceImpl extends BaseServiceImpl<HySupplier, Long> implements HySupplierService {
	@Resource(name = "hySupplierDaoImpl")
	HySupplierDao dao;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name = "hySupplierDaoImpl")
	public void setBaseDao(HySupplierDao dao){
		super.setBaseDao(dao);		
	}	
	
	@Override
	public void save(HySupplier supplier)  {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			if (username != null)
				supplier.setOperator(adminDao.find(username));
		}
		super.save(supplier);
	}
}
