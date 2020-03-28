package com.hongyu.controller.gdw;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.SignContract;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.SignContractService;


import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/admin/signContract/")
public class SignContractController {

	@Resource(name="signContractServiceImpl")
	SignContractService signContractService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,String operatorName,String storeName,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,Integer contractType,Integer status){
		Json json=new Json();
		try {
			List<Filter> filters=new LinkedList<>();
			if(operatorName!=null){
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.like("name", operatorName));
				List<HyAdmin> hyAdmins=hyAdminService.findList(null,filters2,null);
				if(hyAdmins==null||hyAdmins.size()==0){
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new Page<>());
					return json;
				}else{
					filters.add(Filter.in("operator", hyAdmins));
				}
			}
			if(storeName!=null){
				filters.add(Filter.like("storeName", storeName));
			}
			if(startDate!=null){
				filters.add(Filter.ge("startDate", startDate));
			}
			if(endDate!=null){
				filters.add(Filter.le("startDate", endDate));
			}
			if(contractType!=null){
				filters.add(Filter.eq("contractType", contractType));
			}
			if(status!=null){
				filters.add(Filter.eq("status", status));
			}
			pageable.setFilters(filters);
			Page<SignContract> page=signContractService.findPage(pageable);
			List<Map<String, Object>> result=new LinkedList<>();
			for(SignContract tmp:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				map.put("id", tmp.getId());
				map.put("orderSn", tmp.getOrder().getOrderNumber());
				map.put("contractNumber", tmp.getContractNumber());
				map.put("productPN", tmp.getGroup().getGroupLinePn());
				map.put("startDate", tmp.getStartDate());
				map.put("referee", tmp.getReferee());
				map.put("storeName", tmp.getStoreName());
				map.put("operator", tmp.getOperator().getName());
				map.put("contractType", tmp.getContractType());
				map.put("status", tmp.getStatus());
				map.put("createDate", tmp.getCreateDate());
				result.add(map);
			}
			Map<String, Object> hMap=new HashMap<>();
			hMap.put("total", result.size());
			hMap.put("pageNumber", page.getPageNumber());
			hMap.put("pageSize", page.getPageSize());
			hMap.put("rows", result);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			SignContract signContract=signContractService.find(id);
			if(signContract==null){
				json.setSuccess(false);
				json.setMsg("合同不存在");
			}else{
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(signContract);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
