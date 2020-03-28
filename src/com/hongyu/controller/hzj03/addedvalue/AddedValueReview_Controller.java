package com.hongyu.controller.hzj03.addedvalue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.AddedService;
import com.hongyu.entity.AddedServiceAndServiceTransfer;
import com.hongyu.entity.AddedServiceTransfer;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;
import com.hongyu.service.AddedServiceAndServiceTransferService;
import com.hongyu.service.AddedServiceService;
import com.hongyu.service.AddedServiceTransferService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAddedServiceSupplierService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreService;

/** 增值业务付款审核 */
@Controller
@RequestMapping("admin/addedValue")
public class AddedValueReview_Controller {
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "addedServiceTransferServiceImpl")
	AddedServiceTransferService addedServiceTransferService;

	@Resource(name = "addedServiceAndServiceTransferServiceImpl")
	AddedServiceAndServiceTransferService addedServiceAndServiceTransferService;

	@Resource(name = "addedServiceServiceImpl")
	AddedServiceService addedServiceService;

	@Resource(name = "hyAddedServiceSupplierServiceImpl")
	HyAddedServiceSupplierService hyAddedServiceSupplierService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;

	/** 门店增值业务审核 - 列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json subscribeTicketList(Pageable pageable, Integer state, AddedServiceTransfer addedServiceTransfer,
			HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(addedServiceTransfer);
			List<AddedServiceTransfer> addedServiceTransfers = addedServiceTransferService.findList(null, filters,
					null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (AddedServiceTransfer tmp : addedServiceTransfers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);
							m.put("supplerName", tmp.getSupplier().getName());
							Store store = storeService.find(tmp.getStoreId());
							m.put("storeName", store.getStoreName()); 
							m.put("operator", tmp.getOperator().getName());
							m.put("createTime", tmp.getCreateTime());
							m.put("money", tmp.getMoney());
							ans.add(m);
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (AddedServiceTransfer tmp : addedServiceTransfers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);
							m.put("supplerName", tmp.getSupplier().getName());
							Store store = storeService.find(tmp.getStoreId());
							m.put("storeName", store.getStoreName()); 
							m.put("operator", tmp.getOperator().getName());
							m.put("createTime", tmp.getCreateTime());
							m.put("money", tmp.getMoney());
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
					for (AddedServiceTransfer tmp : addedServiceTransfers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);
							m.put("supplerName", tmp.getSupplier().getName());
							Store store = storeService.find(tmp.getStoreId());
							m.put("storeName", store.getStoreName()); 
							m.put("operator", tmp.getOperator().getName());
							m.put("createTime", tmp.getCreateTime());
							m.put("money", tmp.getMoney());
							ans.add(m);
						}
					}
				}

			} else if (state == 1) {// 搜索已完成任务
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();

				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (AddedServiceTransfer tmp : addedServiceTransfers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);
							m.put("supplerName", tmp.getSupplier().getName());
							Store store = storeService.find(tmp.getStoreId());
							m.put("storeName", store.getStoreName()); 
							m.put("operator", tmp.getOperator().getName());
							m.put("createTime", tmp.getCreateTime());
							m.put("money", tmp.getMoney());
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
			answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows)); // 手动分页？
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

	/** 增值业务审核 - 详情 */
	@RequestMapping(value = "detail/view")
	@ResponseBody
	public Json getHistoryComments(Long id) {
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();

		try {
			AddedServiceTransfer a = addedServiceTransferService.find(id);

			// 审核步骤
			String processInstanceId = a.getProcessInstanceId();
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

			obj.put("auditlist", list);

			// 增值业务
			List<AddedService> addedServiceList = new ArrayList<>();
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("addedServiceTransferId", id));
			List<AddedServiceAndServiceTransfer> asastList = addedServiceAndServiceTransferService.findList(null,
					filters, null);
			for (AddedServiceAndServiceTransfer asast : asastList) {
				AddedService addedService = addedServiceService.find(asast.getAddedServiceId());
				addedServiceList.add(addedService);
			}
			obj.put("addedServiceList", addedServiceList);

			// 收款人信息
			obj.put("supplier", a.getSupplier());
			// 申请信息
			obj.put("applyList", a);
			json.setObj(obj);
			json.setMsg("操作成功");
			json.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		return json;
	}

	/** 增值业务审核(门店经理、分公司副总、分公司财务) */
	@RequestMapping("audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();

		try {
			json = addedServiceTransferService.insertAddedValueAudit(id, comment, state, session);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}
}
