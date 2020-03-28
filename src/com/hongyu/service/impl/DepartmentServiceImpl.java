package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.DepartmentDao;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.DepartmentService;
import com.hongyu.util.Constants;
import com.hongyu.util.redis.RedisUtils;
@Service(value = "departmentServiceImpl")
public class DepartmentServiceImpl extends BaseServiceImpl<Department, Long> implements DepartmentService {
    @Resource(name = "redisUtils")
    private RedisUtils redisUtils;
    
	@Resource(name = "departmentDaoImpl")
	DepartmentDao dao;
	
	@Resource(name = "departmentDaoImpl")
	public void setBaseDao(DepartmentDao dao){
		super.setBaseDao(dao);		
	}

	@Override
	public Department findCompanyOfDepartment(Department department) {
		
		while (!department.getIsCompany()) {
			department = department.getHyDepartment();
		}
		
		return department;
	}

	@Override
	public void save(Department department) {
		// TODO Auto-generated method stub
		//新增部门的时候清空部门权限缓存
    	String prex = Constants.TABLE_HY_DEPARTMENT_AUTHORITY + ":" + "*";
    	redisUtils.deleteBypPrex(prex);
		super.save(department);
	}
	
	@Override
	public Department update(Department department) {
		String prex = Constants.TABLE_HY_DEPARTMENT_AUTHORITY + ":" + "*";
    	redisUtils.deleteBypPrex(prex);
    	return super.update(department);
	}
	
	@Override
	public Department update(Department department, String ...ignoreProperties) {
		String prex = Constants.TABLE_HY_DEPARTMENT_AUTHORITY + ":" + "*";
    	redisUtils.deleteBypPrex(prex);
    	return super.update(department, ignoreProperties);
	}
}
