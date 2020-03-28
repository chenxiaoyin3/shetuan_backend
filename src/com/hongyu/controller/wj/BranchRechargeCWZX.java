package com.hongyu.controller.wj;

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
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.BranchRecharge;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.BranchRechargeRecordService;
import com.hongyu.service.BranchRechargeService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.PayDetailsBranchService;
import com.hongyu.service.ReceiptBranchRechargeService;
import com.hongyu.service.ReceiptDetailsService;

/**
 * 财务中心底下分公司充值的展示界面
 * 
 * @author wj
 *
 */
@Transactional
@Controller
@RequestMapping("/admin/cwzxBranchRecharge")
public class BranchRechargeCWZX {

	@Resource(name = "branchBalanceServiceImpl")
	BranchBalanceService branchBalanceService;

	@Resource(name = "branchRechargeServiceImpl")
	BranchRechargeService branchRechargeService;

	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;

	@Resource
	private RuntimeService runtimeService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "receiptBranchRechargeServiceImpl")
	ReceiptBranchRechargeService receiptBranchRechargeService;

	@Resource(name = "receiptDetailsServiceImpl")
	ReceiptDetailsService receiptDetailService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@Resource(name = "branchPreSaveServiceImpl")
	BranchPreSaveService branchPreSaveService;

	@Resource(name = "branchRechargeRecordServiceImpl")
	BranchRechargeRecordService branchRechargeRecordService;
	
	@Resource(name = "payDetailsBranchServiceImpl")
	PayDetailsBranchService payDetailsBranchService;
	

	/**
	 * 获取分公司列表
	 */
	@RequestMapping(value = "/branches")
	@ResponseBody
	public Json getBranches(){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("isHead", 0));
			List<HyCompany> hyCompanies = hyCompanyService.findList(null,filters,null);
			List<Map<String, Object>> res = new ArrayList<>();
			for(HyCompany hyCompany:hyCompanies){
				HashMap<String, Object> map = new HashMap<>();
				map.put("branchId", hyCompany.getHyDepartment().getId());
				map.put("branchName",hyCompany.getHyDepartment().getName());
				res.add(map);
			}
			json.setObj(res);
			json.setMsg("获取成功");
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("获取失败");
			json.setSuccess(false);
		}
		return json;
	}
	
	/**
	 * 分公司充值审核-列表
	 * 
	 * @param pageable
	 * @param state
	 *            //0：未审核 1：已审核 2:全部
	 * @param branchRecharge
	 * @param startTime
	 *            //起始时间
	 * @param endTime
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json branchRechargeReviewList(Pageable pageable, Integer state, Long branchId,
			String startTime, String endTime, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		String treePath = hyAdminService.find(username).getDepartment().getTreePath();
//		String[] strings = treePath.split(","); // string[1] string[2]分公司id
		// System.out.println(username);

		try {
			

//			List<HashMap<String, Object>> res = new ArrayList<>();
//			List<Filter> filters = FilterUtil.getInstance().getFilter(branchRecharge);
			List<Filter> filters = new ArrayList<>();
			
			
			// List<Filter> filters =
			// FilterUtil.getInstance().getFilter(branchRecharge);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(new Filter("createDate", Operator.ge,
						sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(
						new Filter("createDate", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));

			if(branchId!=null){
				filters.add(Filter.eq("branchId", branchId));
			}
			
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			// 总公司
				List<BranchRecharge> branchRecharges = branchRechargeService.findList(null, filters, null);
				if (state == null) { // 搜索所有任务

					List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
					for (Task task : tasks) {
						String processInstanceId = task.getProcessInstanceId();
						for (BranchRecharge tmp : branchRecharges) {
							if (processInstanceId.equals(tmp.getProcessInstanceId())) {
								HashMap<String, Object> m = new HashMap<>();
								m.put("id", tmp.getId());
								m.put("createDate", tmp.getCreateDate());
								m.put("username", hyAdminService.find(tmp.getUsername()).getName());
								m.put("amount", tmp.getAmount());
								m.put("status", tmp.getStatus());
								m.put("branchId", tmp.getBranchId());
								m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
								ans.add(m);
//								if(branchName==null ){
//									m.put("id", tmp.getId());
//									m.put("createDate", tmp.getCreateDate());
//									m.put("username", tmp.getUsername());
//									m.put("amount", tmp.getAmount());
//									m.put("status", tmp.getStatus());
//									m.put("branchId", tmp.getBranchId());
//									m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
//									ans.add(m);
//								}else if(departmentService.find(tmp.getBranchId()).getName().equals(branchName)){
//									m.put("id", tmp.getId());
//									m.put("createDate", tmp.getCreateDate());
//									m.put("username", tmp.getUsername());
//									m.put("amount", tmp.getAmount());
//									m.put("status", tmp.getStatus());
//									m.put("branchId", tmp.getBranchId());
//									m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
//									ans.add(m);
//								}
							}
						}
					}
					List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
							.finished().taskAssignee(username).list();
					for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
						String processInstanceId = historicTaskInstance.getProcessInstanceId();
						for (BranchRecharge tmp : branchRecharges) {
							if (processInstanceId.equals(tmp.getProcessInstanceId())) {
								HashMap<String, Object> m = new HashMap<>();
								m.put("id", tmp.getId());
								m.put("createDate", tmp.getCreateDate());
								m.put("username", hyAdminService.find(tmp.getUsername()).getName());
								m.put("amount", tmp.getAmount());
								m.put("status", tmp.getStatus());
								m.put("branchId", tmp.getBranchId());
								m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
								ans.add(m);
//								if(branchName==null ){
//									m.put("id", tmp.getId());
//									m.put("createDate", tmp.getCreateDate());
//									m.put("username", tmp.getUsername());
//									m.put("amount", tmp.getAmount());
//									m.put("status", tmp.getStatus());
//									m.put("branchId", tmp.getBranchId());
//									m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
//									ans.add(m);
//								}else if(departmentService.find(tmp.getBranchId()).getName().equals(branchName)){
//									m.put("id", tmp.getId());
//									m.put("createDate", tmp.getCreateDate());
//									m.put("username", tmp.getUsername());
//									m.put("amount", tmp.getAmount());
//									m.put("status", tmp.getStatus());
//									m.put("branchId", tmp.getBranchId());
//									m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
//									ans.add(m);
//								}
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
						for (BranchRecharge tmp : branchRecharges) {
							if (processInstanceId.equals(tmp.getProcessInstanceId())) {
								HashMap<String, Object> m = new HashMap<>();
								m.put("id", tmp.getId());
								m.put("createDate", tmp.getCreateDate());
								m.put("username", hyAdminService.find(tmp.getUsername()).getName());
								m.put("amount", tmp.getAmount());
								m.put("status", tmp.getStatus());
								m.put("branchId", tmp.getBranchId());
								m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
								ans.add(m);
//								if(branchName==null ){
//									m.put("id", tmp.getId());
//									m.put("createDate", tmp.getCreateDate());
//									m.put("username", tmp.getUsername());
//									m.put("amount", tmp.getAmount());
//									m.put("status", tmp.getStatus());
//									m.put("branchId", tmp.getBranchId());
//									m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
//									ans.add(m);
//								}else if(departmentService.find(tmp.getBranchId()).getName().equals(branchName)){
//									m.put("id", tmp.getId());
//									m.put("createDate", tmp.getCreateDate());
//									m.put("username", tmp.getUsername());
//									m.put("amount", tmp.getAmount());
//									m.put("status", tmp.getStatus());
//									m.put("branchId", tmp.getBranchId());
//									m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
//									ans.add(m);
//								}
							}
						}
					}

				} else if (state == 1) {// 搜索已完成任务
					List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
							.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username)
							.list();
					for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
						String processInstanceId = historicTaskInstance.getProcessInstanceId();
						for (BranchRecharge tmp : branchRecharges) {
							if (processInstanceId.equals(tmp.getProcessInstanceId())) {
								HashMap<String, Object> m = new HashMap<>();
								m.put("id", tmp.getId());
								m.put("createDate", tmp.getCreateDate());
								m.put("username", hyAdminService.find(tmp.getUsername()).getName());
								m.put("amount", tmp.getAmount());
								m.put("status", tmp.getStatus());
								m.put("branchId", tmp.getBranchId());
								m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
								ans.add(m);
//								if(branchName==null ){
//									m.put("id", tmp.getId());
//									m.put("createDate", tmp.getCreateDate());
//									m.put("username", tmp.getUsername());
//									m.put("amount", tmp.getAmount());
//									m.put("status", tmp.getStatus());
//									m.put("branchId", tmp.getBranchId());
//									m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
//									ans.add(m);
//								}else if(departmentService.find(tmp.getBranchId()).getName().equals(branchName)){
//									m.put("id", tmp.getId());
//									m.put("createDate", tmp.getCreateDate());
//									m.put("username", tmp.getUsername());
//									m.put("amount", tmp.getAmount());
//									m.put("status", tmp.getStatus());
//									m.put("branchId", tmp.getBranchId());
//									m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
//									ans.add(m);
//								}
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

			json.setObj(answer);

			json.setMsg("列表查询成功");
			json.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("列表显示失败");
			json.setSuccess(false);
		}
		return json;
	}

	/**
	 * 分公司充值审核详情页
	 * 
	 * @param id
	 *            //指定查询详情的id
	 * @return
	 */
	@RequestMapping(value = "/list/details")
	@ResponseBody
	public Json branchRechargeReviewListDetail(Long id) {
		Json json = new Json();
		try {
			BranchRecharge branchRecharge = branchRechargeService.find(id);
			if (branchRecharge != null) {

				String processInstanceId = branchRecharge.getProcessInstanceId();
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

				obj.put("accountAlias", branchRecharge.getAccountAlias());
				obj.put("bankName", branchRecharge.getBankName());
				obj.put("bankCode", branchRecharge.getBankCode());
				obj.put("bankType", branchRecharge.getBankType());
				obj.put("bankAccount", branchRecharge.getBankAccount());
				obj.put("branchAccountAlias", branchRecharge.getBranchAccountAlias());
				obj.put("branchBankName", branchRecharge.getBranchBankName());
				obj.put("branchBankCode", branchRecharge.getBranchBankCode());
				obj.put("branchBankType", branchRecharge.getBranchBankType());
				obj.put("branchAccount", branchRecharge.getBranchAccount());
				obj.put("createDate", branchRecharge.getCreateDate());
				obj.put("amount", branchRecharge.getAmount());
				obj.put("remark", branchRecharge.getRemark());
				json.setObj(obj);
			}

			json.setSuccess(true);
			json.setMsg("获取成功");

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查找失败");
			e.printStackTrace();
		}
		return json;
	}


}
