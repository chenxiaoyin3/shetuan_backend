package com.hongyu.controller.gsbing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCreditFhy;
import com.hongyu.entity.HyStoreFhynew;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyCreditFhyService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.HyStoreFhynewService;

@Controller
@RequestMapping("admin/fhyCredit/review")
public class Review_creditFhyController {
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hyStoreFhynewServiceImpl")
	private HyStoreFhynewService hyStoreFhynewService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	@Resource(name="hyRoleServiceImpl")
	private HyRoleService hyRoleService;
	@Resource(name="hyCreditFhyServiceImpl")
	private HyCreditFhyService hyCreditFhyService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@RequestMapping(value = "list/view")
	@ResponseBody
	public Json listview(Pageable pageable,Integer state,String storeName,HttpSession session)
	{
		Json json=new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("type", 1)); //筛选类型为授信
			if(storeName != null){
				filters.add(Filter.like("name", storeName));
			}
			List<HyStoreFhynew> storeFhynewList=hyStoreFhynewService.findList(null,filters,null);
			List<Filter> creditFilter=new ArrayList<Filter>();
			creditFilter.add(Filter.in("hyStoreFhynew", storeFhynewList));
			List<HyCreditFhy> creditList=hyCreditFhyService.findList(null,creditFilter,null);	
			if(creditList.size()==0){
				json.setMsg("查询成功");
			    json.setSuccess(true);
			    json.setObj(new Page<HyCreditFhy>());
			}
			else{
				List<Map<String, Object>> ans = new ArrayList<>();
				Map<String, Object> answer = new HashMap<>();
				if (state == null) {
					List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
					for (Task task : tasks) {
						String processInstanceId = task.getProcessInstanceId();
						for (HyCreditFhy tmp : creditList) {
							if (processInstanceId.equals(tmp.getProcessInstanceId())) {
								HashMap<String, Object> m = new HashMap<>();		
								m.put("id", tmp.getId());
								m.put("storeName", tmp.getHyStoreFhynew().getName());
								m.put("creditMoney", tmp.getMoney());
								m.put("applyTime", tmp.getApplyTime());
								m.put("applyName", tmp.getApplyName().getName());
								m.put("state", 0);// 0未审核	
								m.put("auditStatus", 1); //审核中状态
								ans.add(m);
							}
						}
					}
					List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
							.finished().taskAssignee(username).list();
					for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
						String processInstanceId = historicTaskInstance.getProcessInstanceId();
						for (HyCreditFhy tmp : creditList) {
							if (processInstanceId.equals(tmp.getProcessInstanceId())) {
								HashMap<String, Object> m = new HashMap<>();		
								m.put("id", tmp.getId());
								m.put("storeName", tmp.getHyStoreFhynew().getName());
								m.put("creditMoney", tmp.getMoney());
								m.put("applyTime", tmp.getApplyTime());
								m.put("applyName", tmp.getApplyName().getName());
								m.put("state", 1);// 1已审核	
								m.put("auditStatus",tmp.getAuditStatus());
								ans.add(m);
							}
						}
					}
				}
				/*搜索未完成任务*/
				else if(state==0){
					List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
							.desc().list();
					for (Task task : tasks) {
						String processInstanceId = task.getProcessInstanceId();
						for (HyCreditFhy tmp : creditList) {
							if (processInstanceId.equals(tmp.getProcessInstanceId())) {
								HashMap<String, Object> m = new HashMap<>();
								m.put("id", tmp.getId());
								m.put("storeName", tmp.getHyStoreFhynew().getName());
								m.put("creditMoney", tmp.getMoney());
								m.put("applyTime", tmp.getApplyTime());
								m.put("applyName", tmp.getApplyName().getName());
								m.put("state", 0);// 0未审核	
								m.put("auditStatus", 1); //审核中状态
								ans.add(m);
							}
						}
					}
				}
				/*搜索已完成任务*/
				else if(state==1){
					List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
							.finished().taskAssignee(username).list();
					for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
						String processInstanceId = historicTaskInstance.getProcessInstanceId();
						for (HyCreditFhy tmp : creditList) {
							if (processInstanceId.equals(tmp.getProcessInstanceId())) {
								HashMap<String, Object> m = new HashMap<>();		
								m.put("id", tmp.getId());
								m.put("storeName", tmp.getHyStoreFhynew().getName());
								m.put("creditMoney", tmp.getMoney());
								m.put("applyTime", tmp.getApplyTime());
								m.put("applyName", tmp.getApplyName().getName());
								m.put("state", 1);// 1已审核	
								m.put("auditStatus",tmp.getAuditStatus());
								ans.add(m);
							}
						}
					}
				}
				int page = pageable.getPage();
				int rows = pageable.getRows();
				answer.put("total", ans.size());
				answer.put("pageNumber", page);
				answer.put("pageSize", rows);
				answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows));
				json.setMsg("查询成功");
			    json.setSuccess(true);
			    json.setObj(answer);
			}
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			HyCreditFhy hyCreditFhy=hyCreditFhyService.find(id);
			String processInstanceId = hyCreditFhy.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> list = new LinkedList<>();
			for (Comment comment : commentList) {
				Map<String, Object> map = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				map.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				map.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					map.put("comment", " ");
					map.put("result", 1);
				} else {
					map.put("comment", str.substring(0, index));
					map.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				map.put("time", comment.getTime());

				list.add(map);
			}
			HashMap<String, Object> obj = new HashMap<>();
			obj.put("list", list);
			//授信信息,审核内容
			obj.put("applyName", hyCreditFhy.getApplyName().getName());
			obj.put("storeName", hyCreditFhy.getHyStoreFhynew().getName());
			obj.put("type", "授信");
			obj.put("applyTime", hyCreditFhy.getApplyTime());
			obj.put("creditMoney", hyCreditFhy.getMoney());
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(obj);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("audit")
	@ResponseBody
	public Json audit(Long id,String comment, Integer state, HttpSession session)
	{
		Json json=new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyCreditFhy hyCreditFhy=hyCreditFhyService.find(id);
			String processInstanceId = hyCreditFhy.getProcessInstanceId();	
			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			}
			else{
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			    if(state==1){ //审核通过
			    	HyStoreFhynew hyStoreFhynew=hyCreditFhy.getHyStoreFhynew();
			    	hyStoreFhynew.setCreditMoney(hyStoreFhynew.getCreditMoney().add(hyCreditFhy.getMoney()));
			    	hyStoreFhynewService.update(hyStoreFhynew);
			    	hyCreditFhy.setAuditStatus(2);    	
			    	hyCreditFhyService.update(hyCreditFhy);
			    }
			    else if(state==0){//审核驳回
			    	hyCreditFhy.setAuditStatus(3);
			    	hyCreditFhyService.update(hyCreditFhy);
			    }
			    Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? " " : comment + ":" + state);
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId());
			}
			json.setSuccess(true);
			json.setMsg("操作成功");
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
}
