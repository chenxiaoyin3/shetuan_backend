package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.Questionnaire;
import com.hongyu.entity.QuestionnaireEntry;
import com.hongyu.entity.QuestionnaireFeedback;
import com.hongyu.entity.QuestionnaireScore;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.QuestionnaireEntryService;
import com.hongyu.service.QuestionnaireFeedbackService;
import com.hongyu.service.QuestionnaireScoreService;
import com.hongyu.service.QuestionnaireService;

@Controller
@RequestMapping("/admin/questionnaire")
public class QuestionnaireController {
	@Resource(name="questionnaireServiceImpl")
	QuestionnaireService questionnaireService;
	
	@Resource(name="questionnaireEntryServiceImpl")
	QuestionnaireEntryService questionnaireEntryService;
	
	@Resource(name="questionnaireFeedbackServiceImpl")
	QuestionnaireFeedbackService questionnaireFeedbackService;
	
	@Resource(name="questionnaireScoreServiceImpl")
	QuestionnaireScoreService questionnaireScoreService;
	
	@Resource(name="hyRoleServiceImpl")
	HyRoleService hyRoleService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	static class WrapQuestionnaire{
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		private Long id;
		private String qusetionnaierSn;
		private String name;
		private List<WrapQuestionnaireEntry> wrapQuestionnaireEntries;
		public String getQusetionnaierSn() {
			return qusetionnaierSn;
		}
		public void setQusetionnaierSn(String qusetionnaierSn) {
			this.qusetionnaierSn = qusetionnaierSn;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<WrapQuestionnaireEntry> getWrapQuestionnaireEntries() {
			return wrapQuestionnaireEntries;
		}
		public void setWrapQuestionnaireEntries(List<WrapQuestionnaireEntry> wrapQuestionnaireEntries) {
			this.wrapQuestionnaireEntries = wrapQuestionnaireEntries;
		}
	}
	static class WrapQuestionnaireEntry{
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		private Long id;
		private Long departmentId;//被调查部门
		private String entry;
		public Long getDepartmentId() {
			return departmentId;
		}
		public void setDepartmentId(Long departmentId) {
			this.departmentId = departmentId;
		}
		public String getEntry() {
			return entry;
		}
		public void setEntry(String entry) {
			this.entry = entry;
		}
	}
	@RequestMapping(value="addQuestionnaire")
	@ResponseBody
	public Json addQuestionnaire(@RequestBody WrapQuestionnaire wrapQuestionnaire,HttpSession session){
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Questionnaire questionnaire=new Questionnaire();
			questionnaire.setName(wrapQuestionnaire.getName());
			questionnaire.setQusetionnaierSn(wrapQuestionnaire.getQusetionnaierSn());
			List<QuestionnaireEntry> questionnaireEntries=new LinkedList<>();
			if(wrapQuestionnaire!=null){
				if(wrapQuestionnaire.getWrapQuestionnaireEntries()!=null&&wrapQuestionnaire.getWrapQuestionnaireEntries().size()>0){
					for(WrapQuestionnaireEntry wrapQuestionnaireEntry:wrapQuestionnaire.getWrapQuestionnaireEntries()){
						QuestionnaireEntry qEntry=new QuestionnaireEntry();
						Long departmentId=wrapQuestionnaireEntry.getDepartmentId();
						Department department=departmentService.find(departmentId);
						qEntry.setDepartment(department);
						qEntry.setEntry(wrapQuestionnaireEntry.getEntry());
						qEntry.setQuestionnaire(questionnaire);
						questionnaireEntries.add(qEntry);
					}
				}
			}
			questionnaire.setQuestionnaireEntries(questionnaireEntries);
			questionnaire.setOperator(hyAdmin);
			questionnaire.setDepartment(hyAdmin.getDepartment());
			questionnaireService.save(questionnaire);
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.ne("name", "导游"));
			List<HyRole> hyRoles=hyRoleService.findList(null, filters, null);
			Set<HyAdmin> sets=new HashSet<>();
			for(HyRole hyRole:hyRoles){
				Set<HyAdmin> hyAdmins=hyRole.getHyAdmins();
				for(HyAdmin tmp:hyAdmins){
					if(tmp==null){
						continue;
					}else{
						sets.add(tmp);
					}
				}
			}
			Thread thread=new Thread(new AddFeedbacks(questionnaire,sets));
			thread.start();
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("get/view")
	@ResponseBody
	public Json get(Long id){
		Json json=new Json();
		try {
			Questionnaire questionnaire=questionnaireService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(questionnaire);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping(value="edit")
	@ResponseBody
	public Json edit(@RequestBody WrapQuestionnaire wrapQuestionnaire,HttpSession session){
		Json json=new Json();
		try {
			Questionnaire oldQiestionnaire=questionnaireService.find(wrapQuestionnaire.getId());
			if(oldQiestionnaire!=null){
				oldQiestionnaire.setName(wrapQuestionnaire.getName());
				oldQiestionnaire.setQusetionnaierSn(wrapQuestionnaire.getQusetionnaierSn());
			}
			oldQiestionnaire.getQuestionnaireEntries().clear();
			if(wrapQuestionnaire.getWrapQuestionnaireEntries()!=null&&wrapQuestionnaire.getWrapQuestionnaireEntries().size()>0){
				List<QuestionnaireEntry> questionnaireEntries=new LinkedList<>();
				for(WrapQuestionnaireEntry wEntry:wrapQuestionnaire.getWrapQuestionnaireEntries()){
					QuestionnaireEntry qEntry=new QuestionnaireEntry();
					qEntry.setId(wEntry.getId());
					Long departmentId=wEntry.getDepartmentId();
					Department department=departmentService.find(departmentId);
					qEntry.setDepartment(department);
					qEntry.setQuestionnaire(oldQiestionnaire);
					questionnaireEntries.add(qEntry);
				}
				oldQiestionnaire.getQuestionnaireEntries().addAll(questionnaireEntries);
				
			}
			questionnaireService.update(oldQiestionnaire);
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping(value="delete")
	@ResponseBody
	public Json delete(Long id){
		Json json=new Json();
		try {
			Questionnaire questionnaire=questionnaireService.find(id);
			for(Iterator<QuestionnaireEntry> iterator=questionnaire.getQuestionnaireEntries().iterator();iterator.hasNext();){
				QuestionnaireEntry questionnaireEntry=iterator.next();
				if(questionnaireEntry==null){
					iterator.remove();
				}else{
					questionnaireEntryService.delete(questionnaireEntry);
				}
			}
			questionnaireService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,Questionnaire questionnaire){
		Json json=new Json();
		try {
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);
			pageable.setOrders(orders);
			Page<Questionnaire> page=questionnaireService.findPage(pageable, questionnaire);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
//	@RequestMapping(value="addQuestionnaireEntry")
//	@ResponseBody
//	public Json addQuestionnaireEntry(QuestionnaireEntry questionnaireEntry){
//		Json json=new Json();
//		try {
//			questionnaireEntryService.save(questionnaireEntry);
//			json.setSuccess(true);
//			json.setMsg("添加成功");
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("添加失败: "+e.getMessage());
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//		return json;
//	}
//	@RequestMapping("editQuestionnaireEntry")
//	@ResponseBody
//	public Json editQuestionnaireEntry(QuestionnaireEntry questionnaireEntry){
//		Json json=new Json();
//		try {
//			questionnaireEntryService.update(questionnaireEntry,"questionnaire");
//			json.setSuccess(true);
//			json.setMsg("编辑成功");
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("编辑失败： "+e.getMessage());
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//		return json;
//	}
//	@RequestMapping(value="deleteQuestionnaireEntry")
//	@ResponseBody
//	public Json deleteQuestionnaireEntry(Long id){
//		Json json=new Json();
//		try {
//			questionnaireEntryService.delete(id);
//			json.setSuccess(true);
//			json.setMsg("删除成功");
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("删除失败： "+e.getMessage());
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//		return json;
//	}
	class AddFeedbacks implements Runnable{
		private Questionnaire questionnaire;
		private Set<HyAdmin> feedbackPersons;
		public AddFeedbacks() {
			// TODO Auto-generated constructor stub
		}
		public AddFeedbacks(Questionnaire questionnaire,Set<HyAdmin> feedbackPersons){
			this.questionnaire=questionnaire;
			this.feedbackPersons=feedbackPersons;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(feedbackPersons==null||feedbackPersons.size()==0)return;
			for(HyAdmin hyAdmin:feedbackPersons){
				QuestionnaireFeedback questionnaireFeedback=new QuestionnaireFeedback();
				questionnaireFeedback.setDepartment(hyAdmin.getDepartment());
				questionnaireFeedback.setQuestionnaireSn(questionnaire.getQusetionnaierSn());
				questionnaireFeedback.setFeedbackPerson(hyAdmin);
				List<QuestionnaireScore> questionnaireScores=new ArrayList<>();
				for(Iterator<QuestionnaireEntry>iterator=questionnaire.getQuestionnaireEntries().iterator();iterator.hasNext();){
					QuestionnaireEntry questionnaireEntry=iterator.next();
					if(questionnaireEntry==null){
						iterator.remove();
					}else{
						QuestionnaireScore questionnaireScore=new QuestionnaireScore();
						questionnaireScore.setEntry(questionnaireEntry.getEntry());
						questionnaireScore.setQuestionnaireEntry(questionnaireEntry);
						questionnaireScore.setDepartment(questionnaireEntry.getDepartment());
						questionnaireScore.setFeedbackPerson(hyAdmin);
						questionnaireScoreService.save(questionnaireScore);
						questionnaireScore.setQuestionnaireFeedback(questionnaireFeedback);
						questionnaireScores.add(questionnaireScore);
						
					}
				}
				questionnaireFeedback.setQuestionnaireScores(questionnaireScores);
				questionnaireFeedbackService.save(questionnaireFeedback);
			}
		}
		
	}
}
