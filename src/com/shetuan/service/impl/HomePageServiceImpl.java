package com.shetuan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.shetuan.entity.Organization;
import com.shetuan.service.HomePageService;
import com.shetuan.service.OrganizationService;
@Service("HomePageServiceImpl")
public class HomePageServiceImpl implements HomePageService{
	@Resource(name = "OrganizationServiceImpl")
	OrganizationService	organizationService;
	@Override
	public Json getTotalOrganizationNumber() {
		// TODO Auto-generated method stub
		Json j=new Json();
		try {
			HashMap<String,Object> map=new HashMap<>();
			Filter filter=new Filter("state", Filter.Operator.eq,true );
			Long totalOrganizationNumber=organizationService.count(filter);
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(totalOrganizationNumber);
		}
		catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}
	@Override
	public Json getHotList() {
		// TODO Auto-generated method stub
		Json j=new Json();
		try {
			List<HashMap<String,Object>> result=new ArrayList<>();
			List<Order> order=new ArrayList<>();
			order.add(Order.desc("clickNumber"));
			List<Organization> list=organizationService.findList(10, null, order);
			for(Organization tmp:list) {
				HashMap<String,Object> m=new HashMap<>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("startTime", tmp.getStartTime());
				m.put("place", tmp.getPlace());
				m.put("creator", tmp.getCreator());
				m.put("logoUrl", tmp.getLogoUrl());
				result.add(m);
			}
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(result);
		}
		catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

}
