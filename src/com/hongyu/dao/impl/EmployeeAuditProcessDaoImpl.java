package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.EmployeeAuditProcessDao;
import com.hongyu.entity.EmployeeAuditProcess;

@Repository("employeeAuditProcessDaoImpl")
public class EmployeeAuditProcessDaoImpl extends BaseDaoImpl<EmployeeAuditProcess, Long> implements EmployeeAuditProcessDao{

}
