package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreService;

@Controller
@RequestMapping("/admin/business/businessStoreHyManagement/")
public class BusinessStoreHyManagementController {

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, Store store, String contact) {
		Json json = new Json();
		try {
			System.out.println("contact:"+contact);
			List<Filter> filters = new ArrayList<>();
			if (contact != null) {
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.like("name", contact));
				List<HyAdmin> hyAdmins = hyAdminService.findList(null, filters2, null);
				filters.add(Filter.in("hyAdmin",hyAdmins));
			}
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("registerDate");
			orders.add(order);
			pageable.setOrders(orders);
			Page<Store> page = storeService.findPage(pageable, store);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			Store store = storeService.find(id);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(store);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
