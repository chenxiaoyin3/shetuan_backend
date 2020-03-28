package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.EmployeeRegisterDao;
import com.hongyu.entity.EmployeeRegister;

@Repository("employeeRegisterDaoImpl")
public class EmployeeRegisterDaoImpl extends BaseDaoImpl<EmployeeRegister, Long> implements EmployeeRegisterDao{

}
