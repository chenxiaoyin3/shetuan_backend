package com.hongyu.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Insurance;
import com.hongyu.service.InsuranceService;

@Controller
@RequestMapping("/admin/storeInsurance/")
public class StoreInsuranceController {

	@Resource(name="insuranceServiceImpl")
	InsuranceService insuranceService;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,Insurance insurance,HttpSession session){
		Json json=new Json();
		try {
			Page<Insurance> page=insuranceService.findPage(pageable,insurance);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： "+e.getMessage());
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
			Insurance insurance=insuranceService.find(id);
			if(insurance!=null){
				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(insurance);
			}else{
				json.setSuccess(false);
				json.setMsg("查询失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
}
