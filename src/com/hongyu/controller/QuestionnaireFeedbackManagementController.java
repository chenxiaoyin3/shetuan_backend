package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.QuestionnaireFeedback;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.QuestionnaireFeedbackService;

@Controller
@RequestMapping("/admin/feedbackManagement")
public class QuestionnaireFeedbackManagementController {
	@Resource(name="questionnaireFeedbackServiceImpl")
	QuestionnaireFeedbackService questionnaireFeedbackService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService HyAdminService;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,String feedbackPerson){
		Json json=new Json();
		try {
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.like("name", feedbackPerson));
			List<HyAdmin> hyAdmins=HyAdminService.findList(null,filters,null);
			List<Filter>filters2=new ArrayList<>();
			filters2.add(Filter.in("feedbackPerson", hyAdmins));
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);
			pageable.setOrders(orders);
			Page<QuestionnaireFeedback> page=questionnaireFeedbackService.findPage(pageable);
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
	@RequestMapping("detail")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			QuestionnaireFeedback questionnaireFeedback=questionnaireFeedbackService.find(id);
			if(questionnaireFeedback==null){
				json.setSuccess(false);
				json.setMsg("获取失败");
			}else{
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(questionnaireFeedback);
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
