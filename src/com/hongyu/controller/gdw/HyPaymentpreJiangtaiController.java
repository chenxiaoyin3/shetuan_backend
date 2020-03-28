package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyPaymentpreJiangtai;
import com.hongyu.entity.StoreApplication;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyPaymentpreJiangtaiService;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/hyPaymentpreJiangtai/")
public class HyPaymentpreJiangtaiController {
	@Resource(name = "hyPaymentpreJiangtaiServiceImpl")
	HyPaymentpreJiangtaiService hyPaymentpreJiangtaiService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;

	@RequestMapping("add")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json add(HyPaymentpreJiangtai hyPaymentpreJiangtai, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("applyerName", username);
			// 启动流程
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("PaymentJiangtai", variables);
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 连锁发展员工注册门店任务

			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");

			taskService.complete(task.getId());

			HyAdmin hyAdmin = hyAdminService.find(username);
			hyPaymentpreJiangtai.setProcessInstanceId(pi.getProcessInstanceId());
			hyPaymentpreJiangtai.setOperator(hyAdmin);
			hyPaymentpreJiangtaiService.save(hyPaymentpreJiangtai);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("edit")
	@ResponseBody
	public Json edit(HyPaymentpreJiangtai hyPaymentpreJiangtai) {
		Json json = new Json();
		try {
			hyPaymentpreJiangtaiService.update(hyPaymentpreJiangtai, "createDate", "applicationStatus", "operator",
					"processInstanceId");

			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, Integer status, @DateTimeFormat(iso = ISO.DATE) Date startDate,
			@DateTimeFormat(iso = ISO.DATE) Date endDate, HyPaymentpreJiangtai hyPaymentpreJiangtai,
			HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(hyPaymentpreJiangtai);
			if (status != null && status == 0) {
				filters.add(Filter.ne("applicationStatus", -1));
				filters.add(Filter.ne("applicationStatus", 3));
			} else if (status != null && status == 1) {
				filters.add(Filter.eq("applicationStatus", 3));
			} else if (status != null && status == -1) {
				filters.add(Filter.eq("applicationStatus", -1));
			}
			if (startDate != null) {
				filters.add(Filter.ge("createDate", DateUtil.getStartOfDay(startDate)));
			}
			if (endDate != null) {
				filters.add(Filter.le("createDate", DateUtil.getEndOfDay(endDate)));
			}
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			Set<Long> set = new HashSet<>();
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();

			List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
			List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
					.finished().taskAssignee(username).list();
			Set<String> IDs = new HashSet<>();
			Set<String> taskIDs = new HashSet<>();
			for (Task task : tasks) {
				IDs.add(task.getProcessInstanceId());
				taskIDs.add(task.getProcessInstanceId());
			}
			Set<String> historiceTaskIDs = new HashSet<>();
			for(HistoricTaskInstance historicTaskInstance:historicTaskInstances){
				IDs.add(historicTaskInstance.getProcessInstanceId());
				historiceTaskIDs.add(historicTaskInstance.getProcessInstanceId());
			}
			if (IDs.size() <= 0) {
				answer.put("total", 0);
				answer.put("pageNumber", pageable.getPage());
				answer.put("pageSize", pageable.getRows());
				answer.put("rows", new ArrayList<>());
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(answer);
				return json;
			}
			filters.add(Filter.in("processInstanceId", IDs));
			List<HyPaymentpreJiangtai> hyPaymentpreJiangtais = hyPaymentpreJiangtaiService.findList(null, filters,
					orders);

			for (HyPaymentpreJiangtai tmp : hyPaymentpreJiangtais) {
				if (set.contains(tmp.getId())) {
					continue;
				}
				HashMap<String, Object> m = new HashMap<>();
				m.put("id", tmp.getId());
				if(taskIDs.contains(tmp.getProcessInstanceId())){
					m.put("status", 0);// 0未审核
				}else if(historiceTaskIDs.contains(tmp.getProcessInstanceId())){
					m.put("status", 1);// 1已审核
				}
				m.put("applicationStatus", tmp.getApplicationStatus());
				m.put("createDate", tmp.getCreateDate());
				m.put("modifyDate", tmp.getModifyDate());
				m.put("accountName", tmp.getAccountName());
				m.put("bankAccount", tmp.getBankAccount());
				m.put("bankName", tmp.getBankName());
				m.put("amount", tmp.getAmount());
				m.put("operator", tmp.getOperator());
				m.put("remark", tmp.getRemark());
				ans.add(m);
				set.add(tmp.getId());
				break;
			}
			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", ans.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows));
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(answer);
			return json;
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			HyPaymentpreJiangtai hyPaymentpreJiangtai = hyPaymentpreJiangtaiService.find(id);
			if (hyPaymentpreJiangtai == null) {
				json.setSuccess(false);
				json.setMsg("记录不存在");
			} else {
				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(hyPaymentpreJiangtai);
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找失败： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("cancle")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json cancle(Long id) {
		Json json = new Json();
		try {
			HyPaymentpreJiangtai hyPaymentpreJiangtai = hyPaymentpreJiangtaiService.find(id);
			hyPaymentpreJiangtai.setApplicationStatus(HyPaymentpreJiangtai.cancle);
			hyPaymentpreJiangtaiService.update(hyPaymentpreJiangtai);
			json.setSuccess(true);
			json.setMsg("取消成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("取消失败： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("getHistoryComments/view")
	@ResponseBody
	public Json getHistoryComments(Long id) {
		Json json = new Json();
		try {
			HyPaymentpreJiangtai hyPaymentpreJiangtai = hyPaymentpreJiangtaiService.find(id);
			String processInstanceId = hyPaymentpreJiangtai.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> result = new LinkedList<>();
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

				result.add(map);
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;

	}

}
