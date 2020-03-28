package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.EmployeeRegisterEducationDao;
import com.hongyu.entity.EmployeeRegisterEducation;

@Repository("employeeRegisterEducationDaoImpl")
public class EmployeeRegisterEducationDaoImpl extends BaseDaoImpl<EmployeeRegisterEducation, Long> implements EmployeeRegisterEducationDao{

}
