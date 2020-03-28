package com.hongyu.controller.wj;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
import org.activiti.engine.impl.util.json.HTTP;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.hibernate.annotations.Filters;
import org.hibernate.envers.RevisionEntity;
import org.springframework.stereotype.Controller;
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
import com.hongyu.Filter.Operator;
import com.hongyu.entity.BalanceDueApply;
import com.hongyu.entity.BankList;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.BranchRecharge;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.ReceiptBranchRecharge;
import com.hongyu.service.BalanceDueApplyService;
import com.hongyu.service.BankListService;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.BranchRechargeService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.util.liyang.EmployeeUtil;

import javafx.print.JobSettings;


//分公司充值

@Controller
@RequestMapping("/admin/branchpresave")
public class BranchRechargeController {
	
	@Resource(name = "branchRechargeServiceImpl")
	BranchRechargeService branchRechargeService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@Resource(name = "balanceDueApplyServiceImpl")
	BalanceDueApplyService balanceDueApplyService;
	
	@Resource(name = "branchPreSaveServiceImpl")
	BranchPreSaveService branchPreSaveService;
	
	@Resource(name = "branchBalanceServiceImpl")
	BranchBalanceService branchBalanceService;
	
	@Resource(name = "bankListServiceImpl")
	BankListService bankListService;
	/**
	 * 界面显示的信息
	 * @param httpSession
	 * @return
	 */
	@RequestMapping("/list/detail")    
	@ResponseBody
	public Json companyRechargeList(HttpSession httpSession){
		Json json = new Json();
		String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
//		String treePath = hyAdminService.find(username).getDepartment().getTreePath();
//		String[] strings = treePath.split(",");   //string[1]分公司名称  string[2]部门id
		
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("yhlx",1));
			filters.add(Filter.ne("bankListStatus",0));
			List<BankList> bankLists = bankListService.findList(null,filters,null); 
			List<HashMap<String, Object>> res = new ArrayList<>();
			for(BankList bankList :bankLists){
				HashMap<String, Object> m = new HashMap<>();
				m.put("id", bankList.getId());
				m.put("accountAlias", bankList.getAlias());
				m.put("bankName", bankList.getBankName());
				m.put("bankCode", bankList.getBankCode());
				m.put("bankType", bankList.getBankType());
				m.put("bankAccount", bankList.getBankAccount());
				res.add(m);
			}

			List<Filter> branchFilters = new  ArrayList<>();
			
			branchFilters.add(Filter.eq("hyDepartment", EmployeeUtil.getCompany(hyAdminService.find(username))));
			List<HyCompany> hyCompanys = hyCompanyService.findList(null,branchFilters,null);
			HyCompany hyCompany2 = null;
			Set<BankList> branchbankLists = null;
			if(hyCompanys.size()!=0&&!hyCompanys.isEmpty()){
				hyCompany2 =  hyCompanys.get(0);
				branchbankLists  = hyCompany2.getBankLists();
			}else{
				json.setSuccess(false);
				json.setMsg("公司不存在");
				return json;
			}
			
			List<HashMap<String, Object>> res2 = new ArrayList<>();
			for(BankList branchbankList :branchbankLists){
				if(branchbankList.getBankListStatus()!=0){
					HashMap<String, Object> m = new HashMap<>();
					m.put("id", branchbankList.getId());
					m.put("branchAccountAlias", branchbankList.getAlias());
					m.put("branchBankName", branchbankList.getBankName());
					m.put("branchBankCode", branchbankList.getBankCode());
					m.put("branchBankType", branchbankList.getBankType());
					m.put("branchAccount", branchbankList.getBankAccount());
					res2.add(m);
				}
			}
			
			HashMap<String, Object> map = new HashMap<>();
			map.put("company", res);
			map.put("branch", res2);

			json.setObj(map);
			json.setSuccess(true);
			json.setMsg("获取成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("获取失败");
		}
		
		return json;
	}
	
	/**
	 * 新建充值申请
	 * @param httpSession
	 * @return
	 */
	@RequestMapping("/submit")
	@ResponseBody
	public Json branchRechargeSubmit(Long id,Long branchId,BigDecimal amount,String remark,HttpSession httpSession){
		Json json = new Json();
		try {
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
//			HashMap<String, Object> map = new HashMap<>();
//			map.put("startType", "branch");
//			map.put("startType", "company");
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("branchRecharge");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId())
					.singleResult();
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.claim( task.getId(),username);
			taskService.complete(task.getId());
			
			BankList bankList = bankListService.find(id);
			BankList branchbankList = bankListService.find(branchId);
			BranchRecharge branchRecharge = new BranchRecharge();
			
			branchRecharge.setAccountAlias(bankList.getAlias());
			branchRecharge.setBankAccount(bankList.getBankAccount());
			branchRecharge.setBankCode(bankList.getBankCode());
			branchRecharge.setBankName(bankList.getBankName());
			branchRecharge.setBankType(bankList.getBankType()==true?1:0);
			branchRecharge.setBranchAccount(branchbankList.getBankAccount());
			branchRecharge.setBranchAccountAlias(branchbankList.getAlias());
			branchRecharge.setBranchBankCode(branchbankList.getBankCode());
			branchRecharge.setBranchBankName(branchbankList.getBankName());
			branchRecharge.setBranchBankType(branchbankList.getBankType()==true?1:0);
			branchRecharge.setAmount(amount);
			branchRecharge.setRemark(remark);
			
			branchRecharge.setStatus(1);  //1已提交 待审核  2已审核
			branchRecharge.setUsername(username);
			branchRecharge.setDepartmentId(hyAdminService.find(username).getDepartment().getId());
			branchRecharge.setCreateDate(new Date());
			String treePath = hyAdminService.find(username).getDepartment().getTreePath();
			String[] strings = treePath.split(",");
			if(strings.length>2){
				branchRecharge.setBranchId(Long.parseLong(strings[2]));
			}
			branchRecharge.setProcessInstanceId(task.getProcessInstanceId());
			branchRechargeService.save(branchRecharge);
			
			json.setObj(branchRecharge);
			
			json.setSuccess(true);
			json.setMsg("提交成功");
	
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("提交失败");
			e.printStackTrace();
		}

		return json;
	}
	
	/**
	 * 获取当前登陆人的分公司名称
	 * @param httpSession
	 * @return
	 */
	@RequestMapping("/branchname")
	@ResponseBody
	public Json branchName(HttpSession httpSession){
		Json json = new Json();
		try {
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			String treePath = hyAdminService.find(username).getDepartment().getTreePath();
			String[] strings = treePath.split(",");   //string[1]分公司名称  string[2]部门id
			Long departmentId =Long.valueOf(strings[2]);
			Department department = departmentService.find(departmentId);
			String branchName = department.getName();
			json.setObj(branchName);
			json.setMsg("获取成功");
			json.setSuccess(true);

		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("获取失败");
			json.setSuccess(true);
		}
		return json;
	}
	/**
	 * 获取当前登陆人所在分公司的余额总数
	 * @param session
	 * @return
	 */
	@RequestMapping("/balance")
	@ResponseBody
	public Json balance(HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Long branchId = admin.getDepartment().getHyDepartment().getId();
			List<Filter> filters  = new ArrayList<>();
			filters.add(Filter.eq("branchId", branchId));
			List<BranchBalance> branchBalance = branchBalanceService.findList(null,filters,null);
			BigDecimal balance = new BigDecimal(0);
			if(branchBalance.size()!=0){
				balance = balance.add(branchBalance.get(0).getBranchBalance());
			}
			json.setObj(balance);
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
	 * 获取当前登陆人分公司的冲抵记录
	 * @param page
	 * @param state  1 充值 2抵扣
	 * @param branchName  分公司名称
	 * @param startTime
	 * @param endTime
	 * @param session
	 * @return
	 */
	@RequestMapping("/offsetlist")
	@ResponseBody
	public Json prepayoffsetlist(Pageable page,Integer state,String branchName,
			String startTime, String endTime,HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Long branchId = admin.getDepartment().getHyDepartment().getId();
			
			List<Map<String, Object>> ans = new ArrayList<>();
			HashMap<String, Object> answer = new HashMap<>();
			List<Filter> filters = new ArrayList<>();
			
			
			if(state==1){  //充值
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (startTime != null && !startTime.equals(""))
					filters.add(new Filter("createDate", Operator.ge,
							sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
				if (endTime != null && !endTime.equals(""))
					filters.add(
							new Filter("createDate", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
				filters.add(Filter.eq("branchId", branchId));
				
				page.setFilters(filters);
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("createDate"));
				page.setOrders(orders);
				
				Page<BranchRecharge> branchRecharges = branchRechargeService.findPage(page);
				for (BranchRecharge tmp : branchRecharges.getRows()) {
					HashMap<String, Object> m = new HashMap<>();
//					HyAdmin hyAdmin = hyAdminService.find(tmp.getUsername());
					m.put("id", tmp.getId());
					m.put("createDate", tmp.getCreateDate());
//					m.put("username", hyAdmin.getName());
					m.put("amount", tmp.getAmount());
					m.put("status", tmp.getStatus());
//					m.put("type", 1);
//					m.put("branchId", tmp.getBranchId());
//					m.put("branchName", departmentService.find(tmp.getBranchId()).getName());
					ans.add(m);
				}
				answer.put("total",branchRecharges.getTotal());
				answer.put("pageNumber", page.getPage());
				answer.put("pageSize", page.getRows());
				answer.put("rows", ans);
			
			}else if(state == 2){  //抵扣记录
				
//				if(branchName!=null&&!branchName.equals(""))
//					filters.add(Filter.eq("branchName",branchName));
				filters.add(Filter.eq("branchId", branchId));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (startTime != null && !startTime.equals(""))
					filters.add(new Filter("date", Operator.ge,
							sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
				if (endTime != null && !endTime.equals(""))
					filters.add(
							new Filter("date", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
				filters.add(Filter.ne("type", 1));
				page.setFilters(filters);
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("date"));
				page.setOrders(orders);

				Page<BranchPreSave> branchPreSaves = branchPreSaveService.findPage(page);
				for(BranchPreSave branchPreSave : branchPreSaves.getRows() ){
					HashMap<String, Object> m = new HashMap<>();
					m.put("departmentName", branchPreSave.getDepartmentName());
					m.put("type", branchPreSave.getType());
					m.put("createDate", branchPreSave.getDate());
					m.put("amount", branchPreSave.getAmount());
//					m.put("remark", branchPreSave.getRemark());
					ans.add(m);
				}
				answer.put("total", branchPreSaves.getTotal());
				answer.put("pageNumber", page.getPage());
				answer.put("pageSize", page.getRows());
				answer.put("rows", ans);
			}
			json.setObj(answer);
			json.setMsg("操作成功");
			json.setSuccess(true);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("操作失败");
			json.setSuccess(false);
		}
		return json;
	}
	
	/**
	 * 获取当前充值详情页
	 * @param id
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
