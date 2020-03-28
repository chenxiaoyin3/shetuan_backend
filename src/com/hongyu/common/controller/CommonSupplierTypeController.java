package com.hongyu.common.controller;

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
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierContractService;

@Controller
@RequestMapping("common/supplierType")
public class CommonSupplierTypeController {
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	
	
	@RequestMapping("isInner")
	@ResponseBody
	public Json isInner(HttpSession session)
	{
		Json json=new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("liable", hyAdmin));
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,filters,null);
			if(hySupplierContracts.isEmpty()) {
				throw new Exception("该账号没有负责的供应商合同");
			}
			Boolean type=hySupplierContracts.get(0).getHySupplier().getIsInner();
			if(type==true) {
				json.setObj(1);
			}
			else {
				json.setObj(0);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("查询供应商类型失败");
		}
		return json;
	}
}
