package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;

public interface DepartmentService extends BaseService<Department, Long> {
	
	//add by gxz 缓存加一层
	Department update(Department dpartment);
	Department update(Department department, String ...ignoreProperties);

	//end of add
	void save(Department department);
	Department findCompanyOfDepartment(Department department);
	
}
