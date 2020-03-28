package com.hongyu.util.liyang;

import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;

public class EmployeeUtil {
	/**
	 * 返回该用户的所属公司实体信息。
	 * @param hyAdmin
	 * @return
	 */
	public static Department getCompany(HyAdmin hyAdmin){
		
//		if(hyAdmin.getDepartment().getIsCompany()){
//			return hyAdmin.getDepartment();
//		}
		Department department = hyAdmin.getDepartment();
		while(!department.getIsCompany()){
			//如果当前部门不是公司，就找到他的父部门继续判断。
			department = department.getHyDepartment();
		}
		return department;
	}

}
