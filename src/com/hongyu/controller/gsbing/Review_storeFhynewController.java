package com.hongyu.controller.gsbing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyStoreFhynew;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyCreditFhyService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.HyStoreFhynewService;
@Controller
@RequestMapping("admin/fhyStore/newReview")
public class Review_storeFhynewController {
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
	
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json listview(Pageable pageable,Integer state,HyStoreFhynew queryParam,String startTime, String endTime,HttpSession session)
	{
		Json json=new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(queryParam);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(new Filter("applyTime", Operator.ge,
						sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(
						new Filter("applyTime", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			List<HyStoreFhynew> storeFhynewList=hyStoreFhynewService.findList(null,filters,null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyStoreFhynew tmp : storeFhynewList) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();		
							m.put("id", tmp.getId());
							m.put("name", tmp.getName());
							m.put("person", tmp.getPerson().getName());
							m.put("type", tmp.getType());
							m.put("createTime", tmp.getCreateTime());
							m.put("creator", tmp.getApplyName().getName());
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
					for (HyStoreFhynew tmp : storeFhynewList) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();		
							m.put("id", tmp.getId());
							m.put("name", tmp.getName());
							m.put("person", tmp.getPerson().getName());
							m.put("type", tmp.getType());
							m.put("createTime", tmp.getCreateTime());
							m.put("creator", tmp.getApplyName().getName());
							m.put("state", 1);// 1已审核	
							m.put("auditStatus",tmp.getAuditStatus());
							ans.add(m);
						}
					}
				}
				
				Collections.sort(ans, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						Date date1 = (Date) o1.get("createTime");
						Date date2 = (Date) o2.get("createTime");
						return  date1.compareTo(date2);
					}
				});
				Collections.reverse(ans);
			}
			/*搜索未完成任务*/
			else if(state==0){
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
						.desc().list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyStoreFhynew tmp : storeFhynewList) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("name", tmp.getName());
							m.put("person", tmp.getPerson().getName());
							m.put("type", tmp.getType());
							m.put("createTime", tmp.getCreateTime());
							m.put("creator", tmp.getApplyName().getName());
							m.put("state", 0);// 0未审核	
							m.put("auditStatus", 1); //审核中状态
							ans.add(m);
						}
					}
				}
				Collections.sort(ans, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						Date date1 = (Date) o1.get("createTime");
						Date date2 = (Date) o2.get("createTime");
						return  date1.compareTo(date2);
					}
				});
				Collections.reverse(ans);
			}
			/*搜索已完成任务*/
			else if(state==1){
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyStoreFhynew tmp : storeFhynewList) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();		
							m.put("id", tmp.getId());
							m.put("name", tmp.getName());
							m.put("person", tmp.getPerson().getName());
							m.put("type", tmp.getType());
							m.put("createTime", tmp.getCreateTime());
							m.put("creator", tmp.getApplyName().getName());
							m.put("state", 1);// 1已审核	
							m.put("auditStatus",tmp.getAuditStatus());
							ans.add(m);
						}
					}
				}
				
				Collections.sort(ans, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						Date date1 = (Date) o1.get("createTime");
						Date date2 = (Date) o2.get("createTime");
						return  date1.compareTo(date2);
					}
				});
				Collections.reverse(ans);
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
			HyStoreFhynew storeFhy=hyStoreFhynewService.find(id);
			HashMap<String,Object> storeMap=new HashMap<String,Object>();
    		storeMap.put("name", storeFhy.getName());
    		storeMap.put("person", storeFhy.getPerson().getName());
    		storeMap.put("type", storeFhy.getType());
    		storeMap.put("area", storeFhy.getArea().getFullName());
    		storeMap.put("address", storeFhy.getAddress());
    		storeMap.put("creditMoney", storeFhy.getCreditMoney());
    		storeMap.put("xinyongdaima", storeFhy.getXinyongdaima());
    		storeMap.put("xydmUrl", storeFhy.getXydmUrl());
    		storeMap.put("personName", storeFhy.getPerson().getName());
    		storeMap.put("mobilephone", storeFhy.getPerson().getMobile());
    		storeMap.put("personAccount", storeFhy.getPerson().getUsername());
    		storeMap.put("roleName", storeFhy.getPerson().getRole().getName());
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(storeMap);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("audit")
	@ResponseBody
	public Json audit(Long id,Integer state,String comment,HttpSession session)
	{
		Json json=new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyStoreFhynew storeFhy=hyStoreFhynewService.find(id);
			String processInstanceId = storeFhy.getProcessInstanceId();
			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			}
			else{
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			    if(state==1){ //审核通过
			    	HyAdmin hyAdmin=storeFhy.getPerson();
			    	hyAdmin.setIsEnabled(true);
			    	storeFhy.setAuditStatus(2);
			    	storeFhy.setPerson(hyAdmin);
			    	hyStoreFhynewService.update(storeFhy);
			    }
			    else if(state==0){//审核驳回
			    	storeFhy.setAuditStatus(3);
			    	hyStoreFhynewService.update(storeFhy);
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
