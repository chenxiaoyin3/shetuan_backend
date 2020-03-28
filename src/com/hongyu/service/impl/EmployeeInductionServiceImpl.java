package com.hongyu.service.impl;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.EmployeeInduction;
import com.hongyu.service.EmployeeInductionService;
@Service("employeeInductionServiceImpl")
public class EmployeeInductionServiceImpl extends BaseServiceImpl<EmployeeInduction,Long> implements EmployeeInductionService{
	@Override
	@Resource(name="employeeInductionDaoImpl")
	public void setBaseDao(BaseDao<EmployeeInduction,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
}
