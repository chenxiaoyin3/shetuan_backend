package com.hongyu.controller;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.BusinessSystemSetting;
import com.hongyu.entity.ServicePromise;
import com.hongyu.service.BusinessSystemSettingService;
import com.hongyu.service.ServicePromiseService;

@Controller
@RequestMapping("/ymmall/system_settings")
public class YmmallSystemSettingsController {
	@Resource(name="businessSystemSettingServiceImpl")
	BusinessSystemSettingService systemSettingSrv;
	/**
	 * 获取某一个系统参数
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json systemSettingDetail(String name) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.like("settingName", name));
			filters.add(Filter.eq("isValid", true));
			
			List<BusinessSystemSetting> list = systemSettingSrv.findList(null,filters,null);
			
			if(list==null || list.isEmpty()) {
				throw new Exception("没有有效的参数");
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(list.get(0));
			return json;
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			json.setObj(e.getMessage());
		}
		return json;
	}
	@Resource(name="servicePromiseServiceImpl")
	ServicePromiseService servicePromiseServiceImpl;
	//查看服务承诺详情
	@RequestMapping(value = "/service_promise/detail/view")
	@ResponseBody
	public Json servicePromiseDetail(HttpSession session) {
		Json json = new Json();
		
		try {
			List<ServicePromise> promises = servicePromiseServiceImpl.findAll();
			if (promises.size() > 0 ) {
				json.setMsg("查询成功");
				json.setSuccess(true);
				json.setObj(promises.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("查询失败");
			json.setSuccess(false);
			json.setObj(null);
		}
		
		return json;
	}

}
