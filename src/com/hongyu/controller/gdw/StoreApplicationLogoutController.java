package com.hongyu.controller.gdw;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyPaymentpreJiangtai;
import com.hongyu.entity.PayDeposit;
import com.hongyu.entity.PayDepositBranch;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreApplication;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.PayDepositBranchService;
import com.hongyu.service.PayDepositService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StoreApplicationService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;

@Controller
@RequestMapping("/admin/storeApplicationLogout/")
public class StoreApplicationLogoutController {
	@Resource(name = "payDepositBranchServiceImpl")
	PayDepositBranchService payDepositBranchService;

	@Resource(name = "payDepositServiceImpl")
	PayDepositService payDepositService;

	@Resource(name = "storeApplicationServiceImpl")
	StoreApplicationService storeApplicationService;

	@Resource
	private RuntimeService runtimeService;

	@Resource
	private TaskService taskService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	@Resource
	private HistoryService historyService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			StoreApplication storeApplication = storeApplicationService.find(id);
			json.setSuccess(true);
			json.setMsg("获取失败");
			json.setObj(storeApplication);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, Integer status, String storeName, String contact, String phone,
			StoreApplication storeApplication, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			// Integer status = storeApplication.getStatus();
			if (status != null && status != 0 && status != 1) {
				storeApplication.setApplicationStatus(null);
			}
			List<Filter> filters = FilterUtil.getInstance().getFilter(storeApplication);
			if (storeName != null) {
				List<Filter> filters2 = new LinkedList<>();
				filters2.add(Filter.like("storeName", storeName));
				List<Store> stores = storeService.findList(null, filters2, null);
				if (stores != null && stores.size() > 0) {
					filters.add(Filter.in("store", stores));
				} else {
					Page<List<Map<String, Object>>> page = new Page<>(new LinkedList<List<Map<String, Object>>>(), 0,
							pageable);
					json.setSuccess(true);
					json.setMsg("查找成功");
					json.setObj(page);
					return json;
				}
			}
			List<Filter> filters3 = new LinkedList<>();
			if (contact != null) {
				filters3.add(Filter.like("name", contact));
			}
			if (phone != null) {
				filters3.add(Filter.eq("mobile", phone));
			}
			List<HyAdmin> hyAdmins = hyAdminService.findList(null, filters3, null);
			if (hyAdmins != null && hyAdmins.size() > 0) {
				filters.add(Filter.in("operator", hyAdmins));
			} else {
				Page<List<Map<String, Object>>> page = new Page<>(new LinkedList<List<Map<String, Object>>>(), 0,
						pageable);
				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(page);
				return json;
			}
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createtime");
			orders.add(order);

			filters.add(Filter.eq("type", 4));// 4退出申请

			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
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
				List<StoreApplication> storeApplications = storeApplicationService.findList(null, filters, orders);
				for (StoreApplication tmp : storeApplications) {
					HashMap<String, Object> m = new HashMap<>();
					m.put("id", tmp.getId());
					m.put("applicationStatus", tmp.getApplicationStatus());
					if (taskIDs.contains(tmp.getProcessInstanceId())) {
						m.put("status", 0);// 0未审核
					} else if (historiceTaskIDs.contains(tmp.getProcessInstanceId())) {
						m.put("status", 1);// 1已审核
					}
					m.put("storeName", tmp.getStore() == null ? "" : tmp.getStore().getStoreName());
					m.put("type", tmp.getType());
					m.put("money", tmp.getMoney());
					m.put("balance", tmp.getBalance());
					m.put("pledge", tmp.getPledge());
					m.put("payment", tmp.getPayment());
					m.put("accessory", tmp.getAccessory());
					m.put("contract", tmp.getContract());
					m.put("validDate", tmp.getValidDate());
					m.put("createtime", tmp.getCreatetime());
					m.put("operator", tmp.getOperator());
					ans.add(m);
				}
			} else if (status != null && status == 0) {// 搜索未完成任务
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
				List<StoreApplication> storeApplications = storeApplicationService.findList(null, filters, orders);
				for (StoreApplication tmp : storeApplications) {
					HashMap<String, Object> m = new HashMap<>();
					m.put("id", tmp.getId());
					m.put("applicationStatus", tmp.getApplicationStatus());
					m.put("status", 0);// 0未审核
					m.put("storeName", tmp.getStore() == null ? "" : tmp.getStore().getStoreName());
					m.put("type", tmp.getType());
					m.put("money", tmp.getMoney());
					m.put("balance", tmp.getBalance());
					m.put("pledge", tmp.getPledge());
					m.put("payment", tmp.getPayment());
					m.put("accessory", tmp.getAccessory());
					m.put("contract", tmp.getContract());
					m.put("validDate", tmp.getValidDate());
					m.put("createtime", tmp.getCreatetime());
					m.put("operator", tmp.getOperator());
					ans.add(m);
				}

			} else if (status != null && status == 1) {// 搜索已完成任务
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
				List<StoreApplication> storeApplications = storeApplicationService.findList(null, filters, orders);
				for (StoreApplication tmp : storeApplications) {
					HashMap<String, Object> m = new HashMap<>();
					m.put("id", tmp.getId());
					m.put("applicationStatus", tmp.getApplicationStatus());
					m.put("status", 1);// 已审核
					m.put("storeName", tmp.getStore() == null ? "" : tmp.getStore().getStoreName());
					m.put("type", tmp.getType());
					m.put("money", tmp.getMoney());
					m.put("balance", tmp.getBalance());
					m.put("pledge", tmp.getPledge());
					m.put("payment", tmp.getPayment());
					m.put("accessory", tmp.getAccessory());
					m.put("contract", tmp.getContract());
					m.put("validDate", tmp.getValidDate());
					m.put("createtime", tmp.getCreatetime());
					m.put("operator", tmp.getOperator());
					ans.add(m);
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
			StoreApplication storeApplication = storeApplicationService.find(id);
			String processInstanceId = storeApplication.getProcessInstanceId();
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
				Integer reslut;
				if (index < 0) {
					map.put("comment", " ");
					map.put("result", 1);
				} else {
					map.put("comment", str.substring(0, index));
					reslut = Integer.parseInt(str.substring(index + 1));
					map.put("result", reslut);
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

	public Json audit(Long id, String comment, String special, Integer state, HttpSession session) {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		try {
			StoreApplication storeApplication = storeApplicationService.find(id);
			Store store = storeApplication.getStore();

			String processInstanceId = storeApplication.getProcessInstanceId();
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			Map<String, Object> variables = new HashMap<>();
			Integer status = storeApplication.getApplicationStatus();
			if (state == 1) {
				if (status == 0) {
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("eduleixing", Eduleixing.storeLogout));
					List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
					CommonShenheedu xiane = edu.get(0);
					// Xiane xiane = xianeService.find(Constants.storeLogout);
					if (xiane != null) {
						if (storeApplication.getMoney().compareTo(xiane.getMoney()) <= 0) {
							variables.put("money", "less");
							storeApplication.setApplicationStatus(StoreApplication.vicePresident);// 默认通过审核
						} else {
							variables.put("money", "more");
							storeApplication.setApplicationStatus(StoreApplication.managerCheck);// 连锁经理审核
						}
					} else {
						storeApplication.setApplicationStatus(StoreApplication.managerCheck);// 连锁经理审核

					}

				} else if (status == 1) {
					storeApplication.setApplicationStatus(StoreApplication.vicePresident);// 副总审核
				} else if (status == 2) {
					storeApplication.setApplicationStatus(StoreApplication.complete);// 财务审核,完成
					store.setStatus(Constants.STORE_TUI_CHU);
					store.setPstatus(0);
					store.setMstatus(0);
					// 退出审核通过，把门店所有账号设为无效,把门店部门设置为无效
					Department department = store.getDepartment();
					department.setStatus(0);
					departmentService.update(department);
					Set<HyAdmin> hyAdmins = department.getHyAdmins();
					for (HyAdmin tmp : hyAdmins) {
						tmp.setIsEnabled(false);
						hyAdminService.update(tmp);
					}
					// 门店押金退还审核通过，则需要生成待付款记录,返回前台待付款记录的id

					Integer storeType = storeApplication.getStore().getStoreType();
					if (storeType == 0) { // 0: 虹宇门店
						// 在hy_pay_deposit表中写数据
						PayDeposit payDeposit = new PayDeposit();
						payDeposit.setDepositType(1); // 1.门店保证金退还审核
						payDeposit.setHasPaid(0); // 未付
						payDeposit.setInstitution(storeApplication.getStore().getStoreName());
						payDeposit.setApplyDate(storeApplication.getCreatetime());
						payDeposit.setAppliName(storeApplication.getOperator().getName());
						// payDeposit.setContractCode(); //门店退出没有合同号
						payDeposit.setAmount(storeApplication.getMoney());
						payDeposit.setBalance(storeApplication.getBalance());
						payDeposit.setBankListId(storeApplication.getStore().getBankList().getId());
						payDepositService.save(payDeposit);

						// 返回给前台的数据中加入payDeposit的id
						HashMap<String, Object> obj = new HashMap<>();
						obj.put("id", payDeposit.getId());
						json.setObj(obj);
					} else if (storeType == 1) { // 1:挂靠门店
						PayDepositBranch payDepostiBranch = new PayDepositBranch();
						payDepostiBranch.setHasPaid(0); // 未付
						payDepostiBranch.setInstitution(storeApplication.getStore().getStoreName());
						payDepostiBranch.setApplyDate(storeApplication.getCreatetime());
						payDepostiBranch.setAppliName(storeApplication.getOperator().getName());
						payDepostiBranch.setAmount(storeApplication.getMoney());
						payDepostiBranch.setBalance(storeApplication.getBalance());
						payDepostiBranch.setBankListId(storeApplication.getStore().getBankList().getId());
						payDepostiBranch.setBranchId(storeApplication.getStore().getDepartment().getHyDepartment()
								.getHyDepartment().getId());
						payDepositBranchService.save(payDepostiBranch);
					}
					// 门店余额清零
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("store", store));
					List<StoreAccount> list = storeAccountService.findList(null, filters, null);
					StoreAccount storeAccount = list.get(0);
					synchronized (storeAccount) {
						storeAccount.setBalance(new BigDecimal(0));
						storeAccountService.update(storeAccount);
					}
				}
				storeService.update(store);
				storeApplicationService.update(storeApplication);
				variables.put("msg", "true");
			} else if (state == 2) {

				storeApplication.setApplicationStatus(StoreApplication.bohui);
				storeApplicationService.update(storeApplication);
				store.setPstatus(2);// 已缴纳
				store.setStatus(storeApplication.getPreStatus());
				storeService.update(store);
				variables.put("msg", "false");
			}
			Authentication.setAuthenticatedUserId(username);
			comment = comment == null ? " " : comment;
			comment += ":" + state;
			taskService.addComment(task.getId(), processInstanceId, comment);
			taskService.claim(task.getId(), username);
			taskService.complete(task.getId(), variables);
			json.setSuccess(true);
			json.setMsg("审核成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;

	}
}
