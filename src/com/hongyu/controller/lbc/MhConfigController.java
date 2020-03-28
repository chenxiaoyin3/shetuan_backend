package com.hongyu.controller.lbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.BusinessSettingHistory;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.MhConfig;
import com.hongyu.entity.ServicePromise;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.MhConfigService;

@Controller
@RequestMapping("/admin/mh/config/")
public class MhConfigController {
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "mhConfigServiceImpl")
	MhConfigService mhConfigService;
	
	@RequestMapping("get")
	@ResponseBody
	Json getConfig(HttpSession session) {
		Json json = new Json();
		
		try {
			MhConfig mhConfig = mhConfigService.getConfig();
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("phone", mhConfig.getPhone());
			map.put("companyCode", mhConfig.getCompanyCode());
			map.put("information", mhConfig.getInformation());
			map.put("copyright", mhConfig.getCopyright());
			map.put("introduction", mhConfig.getIntroduction());
			map.put("contactUs", mhConfig.getContactUs());
			map.put("jobWanted", mhConfig.getJobWanted());
			map.put("privacyProtection", mhConfig.getPrivacyProtection());
			
			json.setObj(map);
			json.setSuccess(true);
			json.setMsg("查询成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setObj(null);
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	
	}
	
	@RequestMapping("update")
	@ResponseBody
	Json updateConfig(HttpSession session, MhConfig mhConfig) {
		Json json = new Json();
		
		try {
			//MhConfig mhConfig = mhConfigService.getConfig();
			
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("phone", mhConfig.getPhone());
//			map.put("companyCode", mhConfig.getCompanyCode());
//			map.put("information", mhConfig.getInformation());
//			map.put("copyright", mhConfig.getCopyright());
//			map.put("introduction", mhConfig.getIntroduction());
//			map.put("contactUs", mhConfig.getContactUs());
//			map.put("jobWanted", mhConfig.getJobWanted());
//			map.put("privacyProtection", mhConfig.getPrivacyProtection());
			mhConfigService.updateConfig(mhConfig);
			//json.setObj(map);
			json.setSuccess(true);
			json.setMsg("查询成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setObj(null);
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
}
