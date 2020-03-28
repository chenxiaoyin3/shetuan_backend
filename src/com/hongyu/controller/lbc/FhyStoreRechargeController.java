package com.hongyu.controller.lbc;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.service.FhyStoreAccountLogService;
import com.hongyu.service.FhyStoreAccountService;
import com.hongyu.service.FhyStoreRechargeService;
import com.hongyu.service.HyStoreFhynewService;

@Controller
@RequestMapping("/admin/fhyStoreRecharge/")
public class FhyStoreRechargeController {
	
	@Resource(name="fhyStoreRechargeServiceImpl")
	private FhyStoreRechargeService fhyStoreRechargeService;
	
	@Resource(name="fhyStoreAccountServiceImpl")
	private FhyStoreAccountService fhyStoreAccountService;
	
	@Resource(name="fhyStoreAccountLogServiceImpl")
	private FhyStoreAccountLogService fhyStoreAccountLogService;
	
	@Resource(name="hyStoreFhynewServiceImpl")
	private HyStoreFhynewService hyStoreFhynewService;
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable, HttpSession session)
	{
		Json json=new Json();
		
		 
		
		
		
		
		
		try {
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}	
		return json;
	}
}
