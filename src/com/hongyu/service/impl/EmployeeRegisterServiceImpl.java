package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.EmployeeAuditProcess;
import com.hongyu.entity.EmployeeRegister;
import com.hongyu.service.EmployeeRegisterService;

@Service("employeeRegisterServiceImpl")
public class EmployeeRegisterServiceImpl extends BaseServiceImpl<EmployeeRegister, Long> implements EmployeeRegisterService {
	@Override
	@Resource(name="employeeRegisterDaoImpl")
	public void setBaseDao(BaseDao<EmployeeRegister, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
}
