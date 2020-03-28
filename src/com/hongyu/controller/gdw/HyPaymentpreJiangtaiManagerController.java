package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.Collections;
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
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyPaymentpreJiangtai;
import com.hongyu.entity.PayServicer;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyPaymentpreJiangtaiService;
import com.hongyu.service.PayServicerService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/hyPaymentpreJiangtaiManager/")
public class HyPaymentpreJiangtaiManagerController {
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;

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

	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			HyPaymentpreJiangtai hyPaymentpreJiangtai = hyPaymentpreJiangtaiService.find(id);
			if (hyPaymentpreJiangtai != null) {
				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(hyPaymentpreJiangtai);
			} else {
				json.setSuccess(false);
				json.setMsg("记录不存在");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查找失败： " + e.getMessage());
			e.printStackTrace();
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
			// if (status != null && status == 0) {
			// filters.add(Filter.ne("applicationStatus", -1));
			// filters.add(Filter.ne("applicationStatus", 3));
			// } else if (status != null && status == 1) {
			// filters.add(Filter.eq("applicationStatus", 3));
			// } else if (status != null && status == -1) {
			// filters.add(Filter.eq("applicationStatus", -1));
			// }
			if (startDate != null) {
				filters.add(Filter.ge("createDate", DateUtil.getStartOfDay(startDate)));
			}
			if (endDate != null) {
				filters.add(Filter.le("createDate", DateUtil.getEndOfDay(endDate)));
			}
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);

			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			Set<Long> set = new HashSet<>();
			if (status == null) {
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
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
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
					if (taskIDs.contains(tmp.getProcessInstanceId())) {
						m.put("status", 0);// 0未审核
					} else if (historiceTaskIDs.contains(tmp.getProcessInstanceId())) {
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

			} else if (status == 0) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				Set<String> taskIDs = new HashSet<>();
				for (Task task : tasks) {
					taskIDs.add(task.getProcessInstanceId());
				}
				if (taskIDs.size() <= 0) {
					answer.put("total", 0);
					answer.put("pageNumber", pageable.getPage());
					answer.put("pageSize", pageable.getRows());
					answer.put("rows", new ArrayList<>());
					json.setSuccess(true);
					json.setMsg("获取成功");
					json.setObj(answer);
					return json;
				}
				filters.add(Filter.in("processInstanceId", taskIDs));
				List<HyPaymentpreJiangtai> hyPaymentpreJiangtais = hyPaymentpreJiangtaiService.findList(null, filters,
						orders);
				for (HyPaymentpreJiangtai tmp : hyPaymentpreJiangtais) {
					if (set.contains(tmp.getId())) {
						continue;
					}
					HashMap<String, Object> m = new HashMap<>();
					m.put("id", tmp.getId());
					m.put("status", 0);// 0未审核
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
			} else if (status == 1) {
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				Set<String> historiceTaskIDs = new HashSet<>();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					historiceTaskIDs.add(historicTaskInstance.getProcessInstanceId());
				}
				if (historiceTaskIDs.size() <= 0) {
					answer.put("total", 0);
					answer.put("pageNumber", pageable.getPage());
					answer.put("pageSize", pageable.getRows());
					answer.put("rows", new ArrayList<>());
					json.setSuccess(true);
					json.setMsg("获取成功");
					json.setObj(answer);
					return json;
				}
				filters.add(Filter.in("processInstanceId", historiceTaskIDs));
				List<HyPaymentpreJiangtai> hyPaymentpreJiangtais = hyPaymentpreJiangtaiService.findList(null, filters,
						orders);
				for (HyPaymentpreJiangtai tmp : hyPaymentpreJiangtais) {
					if (set.contains(tmp.getId())) {
						continue;
					}
					HashMap<String, Object> m = new HashMap<>();
					m.put("id", tmp.getId());
					m.put("status", 1);// 1已审核
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
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
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
			int status = hyPaymentpreJiangtai.getApplicationStatus();
			// if (status != HyPaymentpreJiangtai.daiqueren) {
			// json.setSuccess(false);
			// json.setMsg("当前状态不可审核");
			//
			// } else {
			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				Map<String, Object> variables = new HashMap<>();
				if (state == 1) {
					variables.put("msg", "true");
					if (status == HyPaymentpreJiangtai.daiqueren) {// 0待经理审核
						List<Filter> filters = new ArrayList<>();
						filters.add(Filter.eq("eduleixing", Eduleixing.fzjiangtai));
						List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
						CommonShenheedu xiane = edu.get(0);
						// Xiane xiane =
						// xianeService.find(Constants.fzjiangtai);
						if (xiane != null) {
							if (hyPaymentpreJiangtai.getAmount().compareTo(xiane.getMoney()) <= 0) {
								variables.put("money", "less");
								hyPaymentpreJiangtai.setApplicationStatus(HyPaymentpreJiangtai.vicePresidentCheck);
							} else {
								variables.put("money", "more");
								hyPaymentpreJiangtai.setApplicationStatus(HyPaymentpreJiangtai.managerCheck);
							}
						} else {
							variables.put("money", "more");
						}
					} else if (status == HyPaymentpreJiangtai.managerCheck) {// 1经理已审核，待副总审核
						hyPaymentpreJiangtai.setApplicationStatus(HyPaymentpreJiangtai.vicePresidentCheck);
					} else if (status == HyPaymentpreJiangtai.vicePresidentCheck) {// 1副总已审核，待财务审核
						// 添加财务的操作

						hyPaymentpreJiangtai.setApplicationStatus(HyPaymentpreJiangtai.complete);

						// 审核通过后，在hy_pay_servicer表中增加数据
						PayServicer payServicer = new PayServicer();
						payServicer.setReviewId(id);
						payServicer.setHasPaid(0); // 0:未付 1:已付
						payServicer.setType(6); // 1:预付款 2:T+N 3:提前打款
												// 4:旅游元素供应商尾款 5:向酒店/门票/酒加景供应商付款
												// 6:江泰预充值
						payServicer.setApplyDate(hyPaymentpreJiangtai.getCreateDate());
						payServicer.setAppliName(hyPaymentpreJiangtai.getOperator().getName());
						// payServicer.setServicerId(); //无法获取供应商id
						payServicer.setServicerName("江泰保险"); // 供应商名称固定为 “江泰保险”
						payServicer.setAmount(hyPaymentpreJiangtai.getAmount());
						payServicer.setRemark(hyPaymentpreJiangtai.getRemark());
						// payServicer.setBankListId(); //无法获取bankListId
						payServicer.setAccountName(hyPaymentpreJiangtai.getAccountName());
						payServicer.setBankName(hyPaymentpreJiangtai.getBankName());
						payServicer.setBankCode(""); // 无法获取银行联行号
						payServicer.setBankType(1); // 0:对私 1:对公 默认为对公
						payServicer.setBankAccount(hyPaymentpreJiangtai.getBankAccount());

						payServicerService.save(payServicer);

					}
				} else if (state == 0) {
					hyPaymentpreJiangtai.setApplicationStatus(HyPaymentpreJiangtai.cancle);
					variables.put("msg", "false");
				}
				Authentication.setAuthenticatedUserId(username);
				comment = comment == null ? " " : comment;
				comment += ":" + state;
				taskService.addComment(task.getId(), processInstanceId, comment);
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId(), variables);
				hyPaymentpreJiangtaiService.update(hyPaymentpreJiangtai);
				json.setSuccess(true);
				json.setMsg("审核成功");
			}
			// }
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;

	}

}
