package com.hongyu.controller.wj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import org.hibernate.annotations.Filters;
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
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.PrePaySupply;
import com.hongyu.service.BankListService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.PrePaySupplyService;
import com.hongyu.util.ActivitiUtils;


/**
 * 公司预付款审核
 * （包括产品中心审核，副总经理限额审核和总公司财务审核）
 *
 */
@Controller
@RequestMapping("admin/prePay/review")
public class AccountReview_PrePaySupply_Controller {	
	@Resource(name = "hySupplierElementServiceImpl")
	HySupplierElementService hySupplierElementService;
	
	@Resource(name = "bankListServiceImpl")
	BankListService bankListService;
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "prePaySupplyServiceImpl")
	PrePaySupplyService prePaySupplyService;
	
	@Resource(name ="departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private TaskService taskService;
	
	@Resource
	private HistoryService historyService;

	/**
	 * 预付款审核列表页
	 * @param state  //传入 
	 *                 0：审核中 待审核/2：已驳回/1：已通过/3：已付款  /null 全部 
	 *                 
	 * @return
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	public Json supplyList(Pageable pageable, Integer state,HttpSession session) {
		Json  json = new Json();
		try {

			String username = (String) session.getAttribute(CommonAttributes.Principal);
		//	List<Filter> filters = new ArrayList<>();
			List<PrePaySupply> prePaySupplys = prePaySupplyService.findAll();
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) { // 全部
//				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				List<Task> tasks = ActivitiUtils.getTaskList(username,"prePay" );
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (PrePaySupply tmp : prePaySupplys) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("type", tmp.getType());
							m.put("supplierElement", tmp.getSupplierElement().getName());
							m.put("createTime", tmp.getCreateTime());
							m.put("operator", tmp.getOperator().getName());
							m.put("department", tmp.getDepartmentId().getFullName());
							m.put("state", tmp.getState());
							m.put("money", tmp.getMoney());
							m.put("status", 0);

							ans.add(m);
						}
					}
				}
//				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//						.finished().taskAssignee(username).list();
				List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "prePay");
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (PrePaySupply tmp : prePaySupplys) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("type", tmp.getType());
							m.put("supplierElement", tmp.getSupplierElement().getName());
							m.put("createTime", tmp.getCreateTime());
							m.put("operator", tmp.getOperator().getName());
							m.put("department", tmp.getDepartmentId().getFullName());
							m.put("state", tmp.getState());
							m.put("money", tmp.getMoney());
							m.put("status", 1);

							ans.add(m);
						}
					}
				}
//				Collections.sort(ans, new Comparator<Map<String, Object>>() {
//					@Override
//					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//						Date id1 = (Date) o1.get("createTime");
//						Date id2 = (Date) o2.get("createTime");
//						
//						return id2.compareTo(id1)>1 ? 1 : -1;
//					}
//				});
			} else if (state == 0) {// 未审核
//				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
//						.desc().list();
				List<Task> tasks = ActivitiUtils.getTaskList(username,"prePay" );

				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (PrePaySupply tmp : prePaySupplys) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("type", tmp.getType());
							m.put("supplierElement", tmp.getSupplierElement().getName());
							m.put("createTime", tmp.getCreateTime());
							m.put("operator", tmp.getOperator().getName());
							m.put("department", tmp.getDepartmentId().getFullName());
							m.put("state", tmp.getState());
							m.put("money", tmp.getMoney());
							m.put("status", 0);

							ans.add(m);
						}
					}
				}

			} else  {// 搜索已完成任务
//				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//						.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();

				List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "prePay");
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (PrePaySupply tmp : prePaySupplys) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							if(state == 2 && tmp.getState() == 3){
								m.put("id", tmp.getId());
								m.put("type", tmp.getType());
								m.put("supplierElement", tmp.getSupplierElement().getName());
								m.put("createTime", tmp.getCreateTime());
								m.put("operator", tmp.getOperator().getName());
								m.put("department", tmp.getDepartmentId().getFullName());
								m.put("state", tmp.getState());
								m.put("money", tmp.getMoney());
								m.put("status", 1);

								ans.add(m);
							}else if(state ==1 &&  tmp.getState() != 3){
								m.put("id", tmp.getId());
								m.put("type", tmp.getType());
								m.put("supplierElement", tmp.getSupplierElement().getName());
								m.put("createTime", tmp.getCreateTime());
								m.put("operator", tmp.getOperator().getName());
								m.put("department", tmp.getDepartmentId().getFullName());
								m.put("state", tmp.getState());
								m.put("money", tmp.getMoney());
								m.put("status", 1);

								ans.add(m);
							}
							/*m.put("id", tmp.getId());
							m.put("type", tmp.getType());
							m.put("supplierElement", tmp.getSupplierElement().getName());
							m.put("createTime", tmp.getCreateTime());
							m.put("operator", tmp.getOperator().getUsername());
							m.put("department", tmp.getDepartmentId().getFullName());
							m.put("state", tmp.getState());

							ans.add(m);*/

							
							
						}
					}
				}
			}
			Collections.sort(ans, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Date date1 = (Date) o1.get("createTime");
					Date date2 = (Date) o2.get("createTime");
					if(date1==null && date2 != null ) return -1;
					else if(date1!=null && date2 == null) return 1;
					else if(date1 == null && date2 == null) return 0;
					return  date1.compareTo(date2);
				}
			});
			Collections.reverse(ans);
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

	
	
	/**
	 * 预付款申请审核-详情页
	 * @param state
	 * @return
	 */
	@RequestMapping(value = "/detail")
	@ResponseBody
	public Json supplyDetail(Long id) {
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();

		try {
			PrePaySupply prePaySupply = prePaySupplyService.find(id);
			if(prePaySupply == null){
				throw new Exception("找不到该条数据");
			}

			// 审核步骤
			String processInstanceId = prePaySupply.getProcessInstanceId();
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

			obj.put("list", list);

			obj.put("type", prePaySupply.getType());
			obj.put("supplierElement", prePaySupply.getSupplierElement().getName());
			
//			List<Filter> filters = new ArrayList<>();
//			filters.add(Filter.eq("supplierElement", prePaySupply.getSupplierElement()));
//			List<HySupplierElement> lists = hySupplierElementService.findList(null,filters,null);
//			BankList bankList  =lists.get(0).getBankList(); 
			
			obj.put("bankName", prePaySupply.getBankAccount().getBankName());
			obj.put("accountName", prePaySupply.getBankAccount().getAccountName());
			obj.put("bankAccount", prePaySupply.getBankAccount().getBankAccount());
			obj.put("bankCode", prePaySupply.getBankAccount().getBankCode());
			obj.put("bankType", prePaySupply.getBankAccount().getBankType());
			
			obj.put("money", prePaySupply.getMoney());
			obj.put("memo", prePaySupply.getMemo());
			
			json.setObj(obj);
			
			json.setSuccess(true);
			json.setMsg("操作成功");
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败："+e.getMessage());
		}
		return json;
	}
	
	/**
	 * 预付款审核  1通过  0驳回
	 * @param id
	 * @param comment
	 * @param state
	 * @param session
	 * @return
	 */
	@RequestMapping("audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();

		try {
			json  = prePaySupplyService.insertPrePaySupplyAudit(id, comment, state, session);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}
	
	
	
	

}
