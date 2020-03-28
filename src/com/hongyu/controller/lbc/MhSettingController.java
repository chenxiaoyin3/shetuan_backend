package com.hongyu.controller.lbc;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.hongyu.entity.BusinessSystemSetting;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.MhSetting;
import com.hongyu.entity.MhSettingHistory;
import com.hongyu.entity.ServicePromise;
import com.hongyu.service.BusinessSettingHistoryService;
import com.hongyu.service.BusinessSystemSettingService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.MhFuSettingService;
import com.hongyu.service.MhSettingHistoryService;
import com.hongyu.service.MhSettingService;
import com.hongyu.service.ServicePromiseService;

@Controller
@RequestMapping("/admin/mh/settings")
public class MhSettingController {
	@Resource(name="mhSettingServiceImpl")
	MhSettingService mhSettingService;
	
	@Resource(name="mhSettingHistoryServiceImpl")
	MhSettingHistoryService mhSettinghistoryService;
	
	@Resource(name="mhFuSettingServiceImpl")
	MhFuSettingService mhFuSettingService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="servicePromiseServiceImpl")
	ServicePromiseService servicePromiseServiceImpl;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Json systemSettingAdd(MhSetting setting, HttpSession session) {
		Json json = new Json();
		
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			setting.setOperator(admin.getName());
			mhSettingService.save(setting);
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
	public Json systemSettingModify(MhSetting setting) {
		Json json = new Json();
		try {
			MhSetting old_setting = mhSettingService.find(setting.getId());
//			List<Filter> setFilters = new ArrayList<>();
//			setFilters.add(Filter.eq("settingid", old_setting.getId()));
//			setFilters.add(Filter.isNull("endTime"));
//			List<BusinessSettingHistory> settingHistories = historySrv.findList(null,setFilters,null);
//			BusinessSettingHistory oldHistory = null;
//			if(settingHistories!=null && !settingHistories.isEmpty()) {
//				oldHistory = settingHistories.get(0);
//			}
//			oldHistory.setEndTime(new Date());

			
			
			MhSettingHistory history = new MhSettingHistory(old_setting);
			old_setting.setSettingValue(setting.getSettingValue());
			old_setting.setSettingName(setting.getSettingName());
			mhSettingService.update(old_setting);
			mhSettinghistoryService.save(history);
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
	public Json systemSettingPage(MhSetting setting, Pageable pageable) {
		Json json = new Json();
		try {
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<MhSetting> page = mhSettingService.findPage(pageable, setting);
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
			List<MhSettingHistory> list = mhSettinghistoryService.findList(null, filters, new ArrayList<Order>());
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
			MhSetting setting = mhSettingService.find(id);
			setting.setIsValid(false);
			mhSettingService.update(setting);
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
			MhSetting setting = mhSettingService.find(id);
			setting.setIsValid(true);
			mhSettingService.update(setting);
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
			
			List<MhSetting> list = mhSettingService.findList(null,filters,null);
			
			if(list==null || list.isEmpty()) {
				throw new Exception("没有有效的参数");
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			if(name.equals("旅行社荣誉")) {
				List<String> res;
				Map<String, Object> map = new HashMap<>();
				MhSetting mhSetting = list.get(0);
				map.put("id", mhSetting.getId());
				map.put("createTime", mhSetting.getCreateTime());
				map.put("endTime", mhSetting.getEndTime());
				map.put("isValid", mhSetting.getIsValid());
				map.put("operator", mhSetting.getOperator());
				map.put("settingName", mhSetting.getSettingName());
				
				res = Arrays.asList(mhSetting.getSettingValue().split("|"));
				map.put("settingValue", res);
				json.setObj(map);
			}
			else {
				json.setObj(list.get(0));
			}
			
			return json;
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			json.setObj(e.getMessage());
		}
		return json;
	}
	
	
}
