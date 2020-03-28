package com.hongyu.controller.gxz04;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.GroupBiankoudian;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HySupplier;
import com.hongyu.service.GroupBiankoudianService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 采购部审核变更扣点controller
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/caigou/biankoudian/")
public class GroupBiankoudianController {
	
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "groupBiankoudianServiceImpl")
	GroupBiankoudianService groupBiankoudianService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	/**
	 * 审核列表页面，审核变更扣点
	 * @param id
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, GroupBiankoudian groupBiankoudian, String shenheStatus, HttpSession session) {
		Json j = new Json();	
		
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(groupBiankoudian); 
			List<GroupBiankoudian> groupBiankoudians = groupBiankoudianService.findList(null, filters, null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (shenheStatus == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (GroupBiankoudian tmp : groupBiankoudians) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							helpler(tmp, ans, "daishenhe");//待审核数据						
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (GroupBiankoudian tmp : groupBiankoudians) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							
								   List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
									
									String str = "";
									for(Comment c : comment){
										if(username.equals(c.getUserId()))
												{
											str = c.getFullMessage();
											break;
												}
											
									}
									
									String[] strs = str.split(":");
									if(strs[1] == null) {
										throw new RuntimeException("状态错误");
									}
									helpler(tmp, ans, strs[1]);
							
						}
					}
				}
				
			} else if (shenheStatus.equals("daishenhe")) {// 搜索未完成任务
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (GroupBiankoudian tmp : groupBiankoudians) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							helpler(tmp, ans, "daishenhe");//待审核数据		
						}
					}
				}

			} else if (shenheStatus.equals("yishenhe")) {// 搜索已审核任务
				
				List<HistoricTaskInstance> ss = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				Set<HistoricTaskInstance> historicTaskInstances = new HashSet<>(ss);
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (GroupBiankoudian tmp : groupBiankoudians) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
					
								 List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
									
									String str = "";
									for(Comment c : comment){
										if(username.equals(c.getUserId()))
												{
											str = c.getFullMessage();
											break;
												}
											
									}
									
									String[] strs = str.split(":");
									if(strs[1] == null) {
										throw new RuntimeException("状态错误");
									}
									helpler(tmp, ans, strs[1]);							
						   
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
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(answer);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 审核(通过或者驳回)团变更扣点
	 * @param id 团id comment 驳回批注   shenheStatus 审核状态
	 * 		
	 * @return
	 */
	@Transactional
	@RequestMapping(value="audit")
	public Json audit(Long id, String comment, String shenheStatus, HttpSession session) {
		Json json = new Json();

		try {
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				GroupBiankoudian gbkd = groupBiankoudianService.find(id);
				HyGroup hyGroup = gbkd.getGroupId();
				String processInstanceId = gbkd.getProcessInstanceId();

				if (processInstanceId == null || processInstanceId == "") {
					json.setSuccess(false);
					json.setMsg("审核出错，信息不完整，请重新申请");
				} else {
					Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
					
					if (shenheStatus.equals("yitongguo")) {
						if(task.getTaskDefinitionKey().equals("usertask3")) { //最后一步
							gbkd.setAuditStatus(AuditStatus.pass);				
							hyGroup.setKoudianType(gbkd.getNewType());
							
							hyGroup.setPercentageKoudian(gbkd.getNewLiushui());
							hyGroup.setPersonKoudian(gbkd.getNewRentou());
							hyGroup.setIsDisplay(true);
							hyGroup.setIsSpecialKoudian(true);
							hyGroupService.update(hyGroup);
							groupBiankoudianService.update(gbkd);
						}						
						
					
					} else if (shenheStatus.equals("yibohui")) {

							gbkd.setAuditStatus(AuditStatus.notpass);
							hyGroup.setIsDisplay(true);
							hyGroupService.update(hyGroup);
							groupBiankoudianService.update(gbkd);
							
					}
					Authentication.setAuthenticatedUserId(username);
					taskService.claim(task.getId(),username);
					taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
					taskService.complete(task.getId());					
					
					json.setSuccess(true);
					json.setMsg("审核成功");
				}			

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 变更扣点审核详情页面
	 * @param id 变更扣点id
	 * @return 团审核历史记录
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
		Json j = new Json();
		
		try {
			GroupBiankoudian groupBiankoudian = groupBiankoudianService.find(id);
			HyGroup hyGroup = groupBiankoudian.getGroupId();
			HyLine line = hyGroup.getLine();
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("group", hyGroup); //团详情
			map.put("line", line); //线路详情
			
			map.put("changededuct", groupBiankoudian); //变更扣点
	
			/** 审核详情 */
			List<HashMap<String, Object>> shenheMap = new ArrayList<>();
			
				/**
				 * 审核详情添加
				 */
				String processInstanceId = groupBiankoudian.getProcessInstanceId();
				List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
				Collections.reverse(commentList);
				for (Comment comment : commentList) {
					HashMap<String, Object> im = new HashMap<>();
					String taskId = comment.getTaskId();
					HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
							.singleResult();
					String step = "";
					if (task != null) {
						step = task.getName();
					}
					im.put("step", step);
					String username = comment.getUserId();
					HyAdmin hyAdmin = hyAdminService.find(username);
					String name = "";
					if (hyAdmin != null) {
						name = hyAdmin.getName();
					}
					im.put("auditName", name);
					String str = comment.getFullMessage();
					String[] strs = str.split(":");
					
				    im.put("comment", strs[0]);
				    if(strs[1].equals("yitongguo")) {
				    	im.put("result", "通过");
				    } else if (strs[1].equals("yibohui")) {
				    	im.put("result", "驳回");
				    } else {
				    	im.put("result", "提交审核");
				    }
					
					im.put("time", comment.getTime());

					shenheMap.add(im);
				}
				
				map.put("auditRecord", shenheMap);			
			j.setMsg("查看成功");
			j.setSuccess(true);
			j.setObj(map);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	private static void helpler(GroupBiankoudian tmp, List<Map<String, Object>> ans, String status) {
		HyGroup group = tmp.getGroupId();
		HyLine line = group.getLine();
		HashMap<String, Object> m = new HashMap<>();
	
		m.put("id", tmp.getId());	
		m.put("shenheStatus", status);	
		if(line != null) {
			m.put("sn", line.getPn());
			m.put("name", line.getName());
			HySupplier supplier = line.getHySupplier();
			if(supplier != null) {
				m.put("supplierName", supplier.getSupplierName());
			}
		}
		m.put("operator", line.getOperator());
		m.put("applyTime", tmp.getApplyTime());
		m.put("startDay", group.getStartDay());
		
		ans.add(m);
	}
}
