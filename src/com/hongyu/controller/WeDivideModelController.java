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
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.WeDivideModel;
import com.hongyu.entity.WeDivideModelHistory;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.WeDivideModelHistoryService;
import com.hongyu.service.WeDivideModelService;

@Controller
@RequestMapping("/admin/business/dividemodel")

public class WeDivideModelController {
	
	@Resource(name = "weDivideModelServiceImpl")
	WeDivideModelService modelSrv;
	
	@Resource(name = "weDivideModelHistoryServiceImpl")
	WeDivideModelHistoryService modelHistorySrv;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@RequestMapping(value = "/page/view", method = RequestMethod.GET)
	@ResponseBody
	public Json WeDivideModelPage(WeDivideModel model, Pageable pageable) {
		Json json = new Json();
		
		try {
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<WeDivideModel> page = modelSrv.findPage(pageable, model);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
		
		return json;
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Json WeDivideModelAdd(WeDivideModel model, HttpSession session) {
		Json json = new Json();
		
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(new Filter("modelType", Operator.eq, model.getModelType()));
			List<WeDivideModel> list = modelSrv.findList(null, filters, null);
			if (list.size()>0){
				json.setSuccess(false);
				json.setMsg("模型类型已存在");
				json.setObj(null);
				return json;
			}
			
			model.setOperator(admin.getName());
			modelSrv.save(model);
			json.setSuccess(true);
			json.setMsg("添加成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			json.setObj(null);
		}
		
		return json;
	}
	
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	@ResponseBody
	public Json WeDivideModelModify(WeDivideModel model) {
		Json json = new Json();
		
		try {
			Date historyEndTime = new Date();
			WeDivideModelHistory his = new WeDivideModelHistory(modelSrv.find(model.getId()), historyEndTime);
			model.setCreateTime(historyEndTime);
			modelSrv.update(model, "endTime", "modelType", "isValid", "operator");
			modelHistorySrv.save(his);
			json.setSuccess(true);
			json.setMsg("修改成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败");
			json.setObj(null);
		}
		
		return json;
	}
	
	@RequestMapping(value = "/historylist/view", method = RequestMethod.GET)
	@ResponseBody
	public Json WeDivideModelHistory(Long id) {
		Json json = new Json();
		
		try {
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(new Filter("modelId", Operator.eq, id));
			List<WeDivideModelHistory> list = modelHistorySrv.findList(null, filters, new ArrayList<Order>());
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(list);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
		
		return json;
	}
	
	
	
}
