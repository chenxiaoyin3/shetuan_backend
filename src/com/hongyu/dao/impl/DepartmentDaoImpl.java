package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.DepartmentDao;
import com.hongyu.entity.Department;
@Repository("departmentDaoImpl")
public class DepartmentDaoImpl extends BaseDaoImpl<Department, Long> 
implements DepartmentDao{

}
