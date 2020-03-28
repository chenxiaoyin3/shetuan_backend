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
import com.hongyu.dao.ShipDao;
import com.hongyu.entity.Ship;
import com.hongyu.service.ShipService;
@Service(value = "shipServiceImpl")
public class ShipServiceImpl extends BaseServiceImpl<Ship, Long> implements ShipService {
	@Resource(name = "shipDaoImpl")
	ShipDao dao;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name = "shipDaoImpl")
	public void setBaseDao(ShipDao dao){
		super.setBaseDao(dao);		
	}
	
	@Override
	public void save(Ship ship)  {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			if (username != null)
				ship.setDeliverOperator(adminDao.find(username));
		}
		super.save(ship);
	}
}
