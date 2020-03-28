package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.BusinessStore;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.WeBusiness;
import com.hongyu.service.BusinessStoreService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.WeBusinessService;

@Controller
@RequestMapping("/admin/business/businessStoreNonHy/")
public class BusinessStoreNonHyController {

	@Resource(name = "businessStoreServiceImpl")
	BusinessStoreService businessStoreService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			List<Filter> filters = new ArrayList<>();
			Filter filter = Filter.eq("hyAdmin", hyAdmin);
			filters.add(filter);
			List<BusinessStore> lists = businessStoreService.findList(null, filters, null);
			if (lists == null||lists.size()<1) {
				json.setSuccess(false);
				json.setMsg("门店不存在");
			} else {
				BusinessStore businessStore = lists.get(0);
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(businessStore);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

}
