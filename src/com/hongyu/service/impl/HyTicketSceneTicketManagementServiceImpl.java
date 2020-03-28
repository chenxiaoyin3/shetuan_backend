package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketSceneTicketManagementDao;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.service.HyTicketSceneTicketManagementService;

@Service(value="hyTicketSceneTicketManagementServiceImpl")
public class HyTicketSceneTicketManagementServiceImpl extends BaseServiceImpl<HyTicketSceneTicketManagement,Long>
implements HyTicketSceneTicketManagementService{
	@Resource(name = "hyTicketSceneTicketManagementDaoImpl")
	HyTicketSceneTicketManagementDao dao;
	
	@Resource(name = "hyTicketSceneTicketManagementDaoImpl")
	public void setBaseDao(HyTicketSceneTicketManagementDao dao){
		super.setBaseDao(dao);		
	}
}
