package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.xml.soap.Detail;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.QuestionnaireFeedback;
import com.hongyu.entity.QuestionnaireScore;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.QuestionnaireFeedbackService;
import com.hongyu.service.QuestionnaireScoreService;

@Controller
@RequestMapping("/admin/questionnaireFeedback/")
public class QuestionnaireFeedbackController {

	@Resource(name="questionnaireFeedbackServiceImpl")
	QuestionnaireFeedbackService questionnaireFeedbackService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService HyAdminService;
	
	@Resource(name="questionanireScoreServiceImpl")
	QuestionnaireScoreService questionnaireScoreService;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,QuestionnaireFeedback questionnaireFeedback,HttpSession session){
		Json json=new Json();
		try {
			String username=(String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=HyAdminService.find(username);
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("feedbackPerson", hyAdmin));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);
			pageable.setOrders(orders);
			Page<QuestionnaireFeedback>page=questionnaireFeedbackService.findPage(pageable, questionnaireFeedback);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败： "+e.getMessage());
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
			QuestionnaireFeedback questionnaireFeedback=questionnaireFeedbackService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(questionnaireFeedback);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("edit")
	@ResponseBody
	public Json edit(QuestionnaireFeedback questionnaireFeedback,List<QuestionnaireScore> questionnaireScores){
		Json json=new Json();
		try {
			for(QuestionnaireScore questionnaireScore:questionnaireScores){
				questionnaireScoreService.update(questionnaireScore
						,"questionnaireFeedback","questionnaireEntry","entry"
						,"department","feedbackPerson");
			}
			questionnaireFeedback.setFeedbackDate(new Date());
			questionnaireFeedback.setStatus(1);//已填写
			questionnaireFeedbackService.update(questionnaireFeedback
					,"createDate","feedbackPerson","questionnaireSn"
					,"department","questionnaireScores");
			json.setSuccess(true);
			json.setMsg("填写成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("填写错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
