package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Order.Direction;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyDepartmentModelService;

@RestController
@RequestMapping("/settings/department")
public class HyDepartmentController {

	@Resource(name = "departmentServiceImpl")
	DepartmentService  hyDepartmentService;
	
	@RequestMapping(value="list", method = RequestMethod.GET)
	public Json list(){
		Json j = new Json();
		try{	
			/** 找到所有权限信息 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.isNull("hyDepartment");
			Filter filter1 = Filter.eq("status", 1);
			filters.add(filter);
			filters.add(filter1);
			List<Order> orders = new ArrayList<Order>();
			Order order = new Order("id",Direction.asc);
			orders.add(order);
			List<Department> departments = hyDepartmentService.findList(null,filters,orders);
			j.setSuccess(true);
			j.setMsg("查询成功！");
			j.setObj(departments);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;	
	}
	
}
