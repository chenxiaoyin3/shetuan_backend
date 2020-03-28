package com.hongyu.dao.impl;
import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.EmployeeInductionDao;
import com.hongyu.entity.EmployeeInduction;

@Repository("employeeInductionDaoImpl")
public class EmployeeInductionDaoImpl extends BaseDaoImpl<EmployeeInduction,Long> implements EmployeeInductionDao{

}
