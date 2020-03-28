package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupCancelAuditDao;
import com.hongyu.entity.HyGroupCancelAudit;
import com.hongyu.service.HyGroupCancelAuditService;
@Service("hyGroupCancelAuditServiceImpl")
public class HyGroupCancelAuditServiceImpl extends BaseServiceImpl<HyGroupCancelAudit,Long> implements HyGroupCancelAuditService {
	@Resource(name = "hyGroupCancelAuditDaoImpl")
	HyGroupCancelAuditDao dao;
	
	@Resource(name = "hyGroupCancelAuditDaoImpl")
	public void setBaseDao(HyGroupCancelAuditDao dao){
		super.setBaseDao(dao);		
	}	
}
