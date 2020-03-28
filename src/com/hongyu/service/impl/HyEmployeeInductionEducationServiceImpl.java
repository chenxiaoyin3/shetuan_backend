package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyEmployeeInductionEducationDao;
import com.hongyu.entity.HyEmployeeInductionEducation;
import com.hongyu.service.HyEmployeeInductionEducationService;

@Service("hyEmployeeInductionEducationServiceImpl")
public class HyEmployeeInductionEducationServiceImpl extends BaseServiceImpl<HyEmployeeInductionEducation, Long> 
	implements HyEmployeeInductionEducationService
{
	@Resource(name = "hyEmployeeInductionEducationDaoImpl")
	HyEmployeeInductionEducationDao dao;

	@Resource(name = "hyEmployeeInductionEducationDaoImpl")
	public void setBaseDao(HyEmployeeInductionEducationDao dao){
		super.setBaseDao(dao);	
	}
}
