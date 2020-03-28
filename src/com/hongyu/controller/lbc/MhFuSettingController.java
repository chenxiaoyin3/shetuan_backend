package com.hongyu.controller.lbc;


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
import com.hongyu.entity.MhFuSetting;
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
@RequestMapping("/admin/mh/fusettings")
public class MhFuSettingController {

	@Resource(name="mhFuSettingServiceImpl")
	MhFuSettingService mhFuSettingService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Json systemSettingAdd(MhFuSetting setting, HttpSession session) {
		Json json = new Json();
		
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			setting.setOperator(admin.getName());
			mhFuSettingService.save(setting);
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
	public Json systemSettingModify(MhFuSetting setting) {
		Json json = new Json();
		try {
			MhFuSetting old_setting = mhFuSettingService.find(setting.getId());
//			List<Filter> setFilters = new ArrayList<>();
//			setFilters.add(Filter.eq("settingid", old_setting.getId()));
//			setFilters.add(Filter.isNull("endTime"));
//			List<BusinessSettingHistory> settingHistories = historySrv.findList(null,setFilters,null);
//			BusinessSettingHistory oldHistory = null;
//			if(settingHistories!=null && !settingHistories.isEmpty()) {
//				oldHistory = settingHistories.get(0);
//			}
//			oldHistory.setEndTime(new Date());
			old_setting.setSettingValue(setting.getSettingValue());
			old_setting.setSettingName(setting.getSettingName());
			mhFuSettingService.update(old_setting);
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
	public Json systemSettingPage(MhFuSetting setting, Pageable pageable) {
		Json json = new Json();
		try {
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<MhFuSetting> page = mhFuSettingService.findPage(pageable, setting);
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
	
	
	
	@RequestMapping(value = "/invalid", method = RequestMethod.POST)
	@ResponseBody
	public Json systemSettingCInvalid(Long id) {
		Json json = new Json();
		try {
			MhFuSetting setting = mhFuSettingService.find(id);
			setting.setIsValid(false);
			mhFuSettingService.update(setting);
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
			MhFuSetting setting = mhFuSettingService.find(id);
			setting.setIsValid(true);
			mhFuSettingService.update(setting);
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
			
			List<MhFuSetting> list = mhFuSettingService.findList(null,filters,null);
			
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
