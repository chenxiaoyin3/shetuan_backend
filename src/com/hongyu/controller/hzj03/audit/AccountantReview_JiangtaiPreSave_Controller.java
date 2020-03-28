package com.hongyu.controller.hzj03.audit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
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
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyPaymentpreJiangtai;
import com.hongyu.entity.PayServicer;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyPaymentpreJiangtaiService;
import com.hongyu.service.PayServicerService;

/** 财务 - 审核 - 江泰预充款 */
@Controller
@RequestMapping("/admin/accountant/jiangtaiPreSave")
public class AccountantReview_JiangtaiPreSave_Controller {
	
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;

	@Resource(name = "hyPaymentpreJiangtaiServiceImpl")
	HyPaymentpreJiangtaiService hyPaymentpreJiangtaiService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource
	 RuntimeService runtimeService;
	@Resource
	 TaskService taskService;
	@Resource
	 HistoryService historyService;

	/** 江泰预充款-列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json jiangTaiPreSaveReviewList(Pageable pageable, Integer state, HyPaymentpreJiangtai hyPaymentpreJiangtai,
			String startTime, String endTime,HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(hyPaymentpreJiangtai);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(
						new Filter("createDate", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(
						new Filter("createDate", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			
			List<HyPaymentpreJiangtai> hyPaymentpreJiangtais = hyPaymentpreJiangtaiService.findList(null, filters,
					null);
			
			
			
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyPaymentpreJiangtai tmp : hyPaymentpreJiangtais) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);// 0未审核
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
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyPaymentpreJiangtai tmp : hyPaymentpreJiangtais) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);// 1已审核
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
						}
					}
				}
				Collections.sort(ans, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						long id1 = (long) o1.get("id");
						long id2 = (long) o2.get("id");
						return id2 > id1 ? 1 : -1; 
					}
				});
			} else if (state == 0) {// 搜索未完成任务
				
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
						.desc().list();
				
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyPaymentpreJiangtai tmp : hyPaymentpreJiangtais) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);// 0未完成
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
						}
					}
				}

			} else if (state == 1) {// 搜索已完成任务
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();
				
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyPaymentpreJiangtai tmp : hyPaymentpreJiangtais) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);// 1已完成
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
			json.setSuccess(true);
			if (ans.size() == 0) {
				json.setMsg("未获取到符合条件的数据");
			} else
				json.setMsg("获取成功");
			json.setObj(answer);
			return json;
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
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
			
			HashMap<String, Object> obj = new HashMap<>();
			obj.put("obj", result);
			
			//供应商信息  
			obj.put("AccountName", hyPaymentpreJiangtai.getAccountName());
			obj.put("BankName", hyPaymentpreJiangtai.getBankName());
			obj.put("BankCode", ""); //没有银行联行号
			obj.put("BankType", 1);  //默认为对公
			obj.put("BankAccount",hyPaymentpreJiangtai.getBankAccount() );
			
			//审核详情
			obj.put("applier", hyPaymentpreJiangtai.getOperator().getName());
			obj.put("applyTime", hyPaymentpreJiangtai.getCreateDate());
			obj.put("amount", hyPaymentpreJiangtai.getAmount());
			obj.put("remark",hyPaymentpreJiangtai.getRemark() );
			
			
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
		}
		return json;

	}

	@RequestMapping("audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyPaymentpreJiangtai hyPaymentpreJiangtai = hyPaymentpreJiangtaiService.find(id);
			String processInstanceId = hyPaymentpreJiangtai.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (state == 1) {
					hyPaymentpreJiangtai.setApplicationStatus(HyPaymentpreJiangtai.complete);
					
					//审核通过后，在hy_pay_servicer表中增加数据
					PayServicer payServicer = new PayServicer();
					payServicer.setReviewId(id);
					payServicer.setHasPaid(0); // 0:未付  1:已付
					payServicer.setType(6); //1:预付款 2:T+N 3:提前打款 4:旅游元素供应商尾款 5:向酒店/门票/酒加景供应商付款 6:江泰预充值
					payServicer.setApplyDate(hyPaymentpreJiangtai.getCreateDate());
					payServicer.setAppliName(hyPaymentpreJiangtai.getOperator().getName());
					//payServicer.setServicerId();   //无法获取供应商id
					payServicer.setServicerName("江泰保险"); //供应商名称固定为 “江泰保险”
					payServicer.setAmount(hyPaymentpreJiangtai.getAmount());
					payServicer.setRemark(hyPaymentpreJiangtai.getRemark());
					//payServicer.setBankListId();  //无法获取bankListId
					payServicer.setAccountName(hyPaymentpreJiangtai.getAccountName());
					payServicer.setBankName(hyPaymentpreJiangtai.getBankName());
					payServicer.setBankCode(""); //无法获取银行联行号
					payServicer.setBankType(1); //0:对私  1:对公  默认为对公
					payServicer.setBankAccount(hyPaymentpreJiangtai.getBankAccount());
					
					payServicerService.save(payServicer);
					
					
				} else if (state == 0) {
					hyPaymentpreJiangtai.setApplicationStatus(HyPaymentpreJiangtai.cancle);
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? " " : comment + ":" + state);
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId());
				hyPaymentpreJiangtaiService.update(hyPaymentpreJiangtai);
				
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
}
