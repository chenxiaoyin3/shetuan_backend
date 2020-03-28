package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyEmployeeInductionJobDao;
import com.hongyu.entity.HyEmployeeInductionJob;
import com.hongyu.service.HyEmployeeInductionJobService;

@Service("hyEmployeeInductionJobServiceImpl")
public class HyEmployeeInductionJobServiceImpl extends BaseServiceImpl<HyEmployeeInductionJob, Long> 
	implements HyEmployeeInductionJobService
{
	@Resource(name = "hyEmployeeInductionJobDaoImpl")
	HyEmployeeInductionJobDao dao;

	@Resource(name = "hyEmployeeInductionJobDaoImpl")
	public void setBaseDao(HyEmployeeInductionJobDao dao){
		super.setBaseDao(dao);	
	}
}
