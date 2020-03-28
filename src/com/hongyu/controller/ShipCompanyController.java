package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.entity.ShipCompany;
import com.hongyu.service.ShipCompanyService;

@Controller
@RequestMapping("common/shipCompany")
public class ShipCompanyController {
	@Resource(name="shipCompanyServiceImpl")
	ShipCompanyService shipCompanyService;
	
	@RequestMapping("shipList")
	@ResponseBody
	public Json shipList()
	{
		Json json=new Json();
		try {
			List<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
			List<ShipCompany> shipCompanys=shipCompanyService.findAll();
			for(ShipCompany company:shipCompanys) {
				HashMap<String, Object> map=new HashMap<String, Object>();
				map.put("shipCompany", company.getShipCompany());
				list.add(map);
			}
			json.setObj(list);
			json.setSuccess(true);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
