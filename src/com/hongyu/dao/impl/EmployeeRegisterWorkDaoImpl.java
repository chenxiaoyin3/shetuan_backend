package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.EmployeeRegisterWorkDao;
import com.hongyu.entity.EmployeeRegisterWork;

@Repository("employeeRegisterWorkDaoImpl")
public class EmployeeRegisterWorkDaoImpl extends BaseDaoImpl<EmployeeRegisterWork, Long> implements EmployeeRegisterWorkDao {

}
