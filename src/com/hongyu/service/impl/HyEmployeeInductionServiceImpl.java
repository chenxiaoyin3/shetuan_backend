package com.hongyu.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyEmployeeInductionDao;
import com.hongyu.entity.HyEmployeeInduction;
import com.hongyu.service.HyEmployeeInductionService;

@Service("hyEmployeeInductionServiceImpl")
public class HyEmployeeInductionServiceImpl extends BaseServiceImpl<HyEmployeeInduction,Long> 
implements HyEmployeeInductionService{
	@Resource(name = "hyEmployeeInductionDaoImpl")
	HyEmployeeInductionDao dao;

	@Resource(name = "hyEmployeeInductionDaoImpl")
	public void setBaseDao(HyEmployeeInductionDao dao){
		super.setBaseDao(dao);	
	}
}
