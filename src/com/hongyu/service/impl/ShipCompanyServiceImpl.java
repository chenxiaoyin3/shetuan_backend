package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ShipCompanyDao;
import com.hongyu.entity.ShipCompany;
import com.hongyu.service.ShipCompanyService;

@Service("shipCompanyServiceImpl")
public class ShipCompanyServiceImpl extends BaseServiceImpl<ShipCompany,Long> implements ShipCompanyService{
	@Resource(name="shipCompanyDaoImpl")
	ShipCompanyDao shipCompanyDao;
	
	@Resource(name="shipCompanyDaoImpl")
	public void setBaseDao(ShipCompanyDao dao) {
		super.setBaseDao(dao);
	}
}
