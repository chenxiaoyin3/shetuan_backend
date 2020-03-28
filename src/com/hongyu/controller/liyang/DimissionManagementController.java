package com.hongyu.controller.liyang;

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
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.lucene.store.FSDirectory;
import org.bouncycastle.jce.provider.BrokenJCEBlockCipher.BrokePBEWithMD5AndDES;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDimissionAudit;
import com.hongyu.entity.PayablesBranchsettle;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDimissionAuditService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.util.liyang.EmployeeUtil;
/**
 * 离职管理
 * @author li_yang
 *
 */
@Controller
@RequestMapping("/admin/dimissionManagement/")
public class DimissionManagementController {
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "hyDimissionAuditServiceImpl")
	HyDimissionAuditService hyDimissionAuditService;
	
	/**
	 * 离职办理列表接口
	 * 应该是查看所有的离职申请记录
	 * @param pageable
	 * @param status   审核状态
	 * @return
	 */
	@RequestMapping(value="getDimissionAudits")
	@ResponseBody
	public Json listview(Pageable pageable,Integer status,HttpSession session){
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<Map<String, Object>> result=new LinkedList<>();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			//判断条件，那个角色才能看到所有的离职申请信息。
			if(hyAdmin.getRole().getName()=="总公司行政部门经理"){
				List<Filter> filters = new ArrayList<>();
				//显示所有的已提交的离职信息
				filters.add(Filter.ne("status", 0));
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("createDate"));
				pageable.setFilters(filters);
				pageable.setOrders(orders);
				Page<HyDimissionAudit> page = hyDimissionAuditService.findPage(pageable);	
				for(HyDimissionAudit tmp : page.getRows()){
					Map<String,Object> map = new HashMap<>();
					map.put("applicant", tmp.getApplicant().getName());
					map.put("department", tmp.getApplicant().getDepartment());
					map.put("company", EmployeeUtil.getCompany(tmp.getApplicant()).getName());
					map.put("applicationTime", tmp.getApplicationTime());
					map.put("dimissionTime", tmp.getDimissionTime());
					map.put("status", tmp.getStatus());
					result.add(map);
				}
				hm.put("total", page.getTotal());
				hm.put("pageNumber", page.getPageNumber());
				hm.put("pageSize", page.getPageSize());
				hm.put("rows", result);
				json.setMsg("查询成功");
				json.setSuccess(true);
				json.setObj(hm);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
		}
		return json;
	}
	/**
	 * 保存离职办理信息
	 * 员工自己新建好离职申请，可以先保存但不提交
	 * @param dimissionAudit
	 * @return
	 */
	@RequestMapping(value="saveDimissionAudit")
	@ResponseBody
	public Json save(HyDimissionAudit dimissionAudit,HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			dimissionAudit.setApplicant(hyAdmin);
			dimissionAudit.setStatus(0);
			hyDimissionAuditService.save(dimissionAudit);
			json.setSuccess(true);
			json.setMsg("保存成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存失败");
			
		}
		return json;
	}
	/**
	 * 查询离职信息
	 * 员工查看自己保存或者提交的离职信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value="getDimissionAudit")
	@ResponseBody
	public Json getDimissionById(Long id,HttpSession session){
		Json json = new Json();
		try {
			/*String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			//查询当前用户自己的离职申请信息
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("applicant", hyAdmin));
			List<HyDimissionAudit> hyDimissionAudits = hyDimissionAuditService.findList(null,filters,null);
			List<Map<String, Object>> result=new LinkedList<>();
			for(HyDimissionAudit tmp : hyDimissionAudits){
				Map<String,Object> map = new HashMap<>();
				map.put("applicant", tmp.getApplicant().getName());
				map.put("department", tmp.getApplicant().getDepartment());
				map.put("company", EmployeeUtil.getCompany(tmp.getApplicant()).getName());
				map.put("applicationTime", tmp.getApplicationTime());
				map.put("dimissionTime", tmp.getDimissionTime());
				map.put("status", tmp.getStatus());
				result.add(map);
			}*/
			HyDimissionAudit hyDimissionAudit = hyDimissionAuditService.find(id);
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(hyDimissionAudit);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
		}
		return json;
	}
	/**
	 * 删除离职信息
	 * 员工可以删除自己未提交或者被驳回的离职信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value="deleteDimissionAudit")
	@ResponseBody
	public Json delete(Long id){
		Json json = new Json();
		try {
			HyDimissionAudit hyDimissionAudit = hyDimissionAuditService.find(id);
			hyDimissionAuditService.delete(hyDimissionAudit);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败");
		}
		return json;
	}
	/**
	 * 获取审核详情页
	 * @param id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json = new Json();
		try {
			Map<String, Object> hm = new HashMap<>();
			
			HyDimissionAudit hyDimissionAudit = hyDimissionAuditService.find(id);
			hm.put("dimissionAudit",hyDimissionAudit);
			String processInstanceId = hyDimissionAudit.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> auditList = new LinkedList<>();
			//审核信息
			for (Comment comment : commentList) {
				Map<String, Object> auditMap = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				auditMap.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				auditMap.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					auditMap.put("comment", " ");
					auditMap.put("result", 1);
				} else {
					auditMap.put("comment", str.substring(0, index));
					auditMap.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				auditMap.put("time", comment.getTime());
				auditList.add(auditMap);
			}
			hm.put("auditList", auditList);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
		}
		return json;
	}
	/**
	 * 提交离职申请
	 * 员工提交自己的离职申请去审核
	 * @param id
	 * @return
	 */
	@RequestMapping(value="submitDimissionAudit")
	@ResponseBody
	public Json submit(Long id,HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			//开启工作流
			ProcessInstance pInstance = runtimeService.startProcessInstanceByKey("dimissionManagement");
			Task task = taskService.createTaskQuery().processInstanceId(pInstance.getProcessInstanceId()).singleResult();
			HyDimissionAudit hyDimissionAudit = hyDimissionAuditService.find(id);
			//状态设置为已提交审核
			hyDimissionAudit.setStatus(1);
			hyDimissionAudit.setProcessInstanceId(pInstance.getProcessInstanceId());
			hyDimissionAuditService.update(hyDimissionAudit);
			// 完成提交
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pInstance.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			json.setMsg("提交申请成功");
		    json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("提交失败");
		}
		return json;
	}
	/**
	 * 员工保存并提交这个离职申请
	 * @param dimissionAudit
	 * @return
	 */
	@RequestMapping(value="saveAndSubmitDimissionAudit")
	@ResponseBody
	public Json saveAndsubmit(HyDimissionAudit dimissionAudit,HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			//先保存
			dimissionAudit.setApplicant(hyAdmin);
			dimissionAudit.setStatus(0);
			
			//开启工作流，提交申请
			ProcessInstance pInstance = runtimeService.startProcessInstanceByKey("dimissionManagement");
			Task task = taskService.createTaskQuery().processInstanceId(pInstance.getProcessInstanceId()).singleResult();
			//状态设置为已提交审核
			dimissionAudit.setStatus(1);
			dimissionAudit.setProcessInstanceId(pInstance.getProcessInstanceId());
			hyDimissionAuditService.save(dimissionAudit);
			// 完成提交
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pInstance.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			json.setMsg("保存并提交申请成功");
		    json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存并提交失败");
		}
		return json;
	}
	
	@RequestMapping(value="processDimissionAudit")
	@ResponseBody
	public Json audit(Long id,String comment, Integer state, HttpSession session)
	{
		Json json=new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyDimissionAudit hyDimissionAudit = hyDimissionAuditService.find(id);
			String processInstanceId = hyDimissionAudit.getProcessInstanceId();
			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			}
			else{
				HashMap<String, Object> map = new HashMap<>();
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();

				if (state == 1){//审核通过
					map.put("result", "tongguo");
					if(hyDimissionAudit.getStepName()==1){//提交,代部门经理审核
						hyDimissionAudit.setStepName(2);//待行政中心经理审核
					}
					else if(hyDimissionAudit.getStepName()==2){
						hyDimissionAudit.setStatus(2); //设置审核状态为已通过
						hyDimissionAudit.setStepName(3); //行政中心经理审核通过
						HyAdmin hyAdmin = hyAdminService.find(hyDimissionAudit.getApplicant().getUsername());
						//设置为离职状态
						hyAdmin.setIsOnjob(false);
						hyAdminService.update(hyAdmin);
					
					}
				}
				else if(state==0){//驳回
					map.put("result", "bohui");
					hyDimissionAudit.setPassAudit(2);//设置状态为已驳回
					hyDimissionAudit.setStatus(3);; //设置整个离职信息为驳回状态
				}
			
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId, (comment == null ? " " : comment) + ":" + state);
				taskService.complete(task.getId(), map);
				hyDimissionAuditService.update(hyDimissionAudit);
				json.setMsg("审核成功");
			    json.setSuccess(true);
			}
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}

}
