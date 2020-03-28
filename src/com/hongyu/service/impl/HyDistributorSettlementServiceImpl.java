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
import com.hongyu.dao.HyDistributorSettlementDao;
import com.hongyu.entity.HyDistributorManagement;
import com.hongyu.entity.HyDistributorSettlement;
import com.hongyu.service.HyDistributorSettlementService;

@Service("hyDistributorSettlementServiceImpl")
public class HyDistributorSettlementServiceImpl extends BaseServiceImpl<HyDistributorSettlement,Long>
        implements HyDistributorSettlementService{
	@Resource(name = "hyDistributorSettlementDaoImpl")
	HyDistributorSettlementDao dao;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name = "hyDistributorSettlementDaoImpl")
	public void setBaseDao(HyDistributorSettlementDao dao){
		super.setBaseDao(dao);	
	}
	@Override
	public void save(HyDistributorSettlement hyDistributorSettlement){
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();	
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			if (username != null)
				hyDistributorSettlement.setOperator(adminDao.find(username));
		}
		super.save(hyDistributorSettlement);
	}
}
