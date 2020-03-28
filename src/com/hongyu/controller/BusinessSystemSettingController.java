package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.hongyu.entity.BusinessSystemSetting;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.ServicePromise;
import com.hongyu.service.BusinessSettingHistoryService;
import com.hongyu.service.BusinessSystemSettingService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.ServicePromiseService;

@Controller
@RequestMapping("/admin/business/settings")
public class BusinessSystemSettingController {
	@Resource(name="businessSystemSettingServiceImpl")
	BusinessSystemSettingService systemSettingSrv;
	
	@Resource(name="businessSettingHistoryServiceImpl")
	BusinessSettingHistoryService historySrv;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="servicePromiseServiceImpl")
	ServicePromiseService servicePromiseServiceImpl;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Json systemSettingAdd(BusinessSystemSetting setting, HttpSession session) {
		Json json = new Json();
		
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			setting.setOperator(admin.getName());
			systemSettingSrv.save(setting);
			json.setSuccess(true);
			json.setMsg("添加成功");
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("添加失败");
		}
		return json;
	}
	
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	@ResponseBody
	public Json systemSettingModify(BusinessSystemSetting setting) {
		Json json = new Json();
		try {
			Date historyEndTime = new Date();
			BusinessSettingHistory history = new BusinessSettingHistory(systemSettingSrv.find(setting.getId()), historyEndTime);
			setting.setCreateTime(historyEndTime);
			systemSettingSrv.update(setting, "endTime", "isValid", "operator");
			historySrv.save(history);
			json.setSuccess(true);
			json.setMsg("修改成功");
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("修改失败");
		}
		return json;
	}
	
	@RequestMapping(value = "/page/view", method = RequestMethod.GET)
	@ResponseBody
	public Json systemSettingPage(BusinessSystemSetting setting, Pageable pageable) {
		Json json = new Json();
		try {
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<BusinessSystemSetting> page = systemSettingSrv.findPage(pageable, setting);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
		return json;
	}
	
	@RequestMapping(value = "/historylist/view", method = RequestMethod.GET)
	@ResponseBody
	public Json systemSettingHistory(Long id) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			Filter filter = new Filter("settingid", Operator.eq, id);
			filters.add(filter);
			List<BusinessSettingHistory> list = historySrv.findList(null, filters, new ArrayList<Order>());
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(list);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
		return json;
	}
	
	@RequestMapping(value = "/invalid", method = RequestMethod.POST)
	@ResponseBody
	public Json systemSettingCInvalid(Long id) {
		Json json = new Json();
		try {
			BusinessSystemSetting setting = systemSettingSrv.find(id);
			setting.setIsValid(false);
			systemSettingSrv.update(setting);
			json.setSuccess(true);
			json.setMsg("添加成功");
			json.setObj(null);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("添加失败");
			json.setObj(null);
		}
		return json;
	}
	
	@RequestMapping(value = "/valid", method = RequestMethod.POST)
	@ResponseBody
	public Json systemSettingValid(Long id) {
		Json json = new Json();
		try {
			BusinessSystemSetting setting = systemSettingSrv.find(id);
			setting.setIsValid(true);
			systemSettingSrv.update(setting);
			json.setSuccess(true);
			json.setMsg("添加成功");
			json.setObj(null);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("添加失败");
			json.setObj(null);
		}
		return json;
	}
	
	//查看服务承诺详情
	@RequestMapping(value = "/servicepromise/detail/view")
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
			e.printStackTrace();
			json.setMsg("查询失败");
			json.setSuccess(false);
			json.setObj(null);
		}
		
		return json;
	}
	
	
	@RequestMapping(value = "/servicepromise/modify")
	@ResponseBody
	public Json servicePromiseModify(HttpSession session, ServicePromise promise) {
		Json json = new Json();
		
		try {
			servicePromiseServiceImpl.update(promise);
			json.setMsg("修改成功");
			json.setSuccess(true);
			json.setObj(null);
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("修改失败");
			json.setSuccess(false);
			json.setObj(null);
		}
		
		return json;
	}
	
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
	
	
}
