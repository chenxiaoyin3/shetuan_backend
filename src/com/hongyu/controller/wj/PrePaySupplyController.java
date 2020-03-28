package com.hongyu.controller.wj;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventListenerProxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.editor.ui.SelectEditorComponent.EditorSelectedListener;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.hibernate.annotations.Filters;
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
import com.hongyu.controller.WeBusinessDivideController.WrapBusinessDivideDetail;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchRecharge;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.entity.PrePaySupply;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.BankListService;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.PrePaySupplyService;
import com.hongyu.service.ReceiptBranchRechargeService;
import com.hongyu.util.liyang.EmployeeUtil;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.TransducedAccessor_field_Long;

import java.util.Collections;

@Controller
@RequestMapping("/admin/branchprepay")
public class PrePaySupplyController {
	@Resource(name = "branchBalanceServiceImpl")
	BranchBalanceService branchBalanceService;

	@Resource(name = "hySupplierElementServiceImpl")
	HySupplierElementService hySupplierElementService;

	@Resource(name = "bankListServiceImpl")
	BankListService bankListService;

	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource(name = "prePaySupplyServiceImpl")
	PrePaySupplyService prePaySupplyService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource
	private RuntimeService runtimeService;

	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource(name = "receiptBranchRechargeServiceImpl")
	ReceiptBranchRechargeService receiptBranchRechargeService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	CommonEdushenheService commonEdushenheService;
	
	
	
	/**
	 * 列表页
	 * 
	 * @param state
	 *            //未审核 已通过 已驳回
	 * @return
	 */
	@RequestMapping("/lists")
	@ResponseBody
	public Json supplyList(Pageable pageable,Integer state, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			System.out.println("username : "+username);
			Map<String, Object> answer = new HashMap<>();

			List<Filter> filters = new ArrayList<>();
			if(state != null){
				if(state == 2){ //已驳回
					filters.add(Filter.eq("state", 3));
				}else if(state == 0){ //待审核
					filters.add(Filter.eq("state", state));
				}else{ //已审核
					filters.add(Filter.le("state", 2));
					filters.add(Filter.ge("state", 1));
				}
				
			}
			
			filters.add(Filter.eq("operator", hyAdminService.find(username)));
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createTime");
			orders.add(order);
			List<PrePaySupply> lists = prePaySupplyService.findList(null, filters, orders);			
			
			
			List<HashMap<String, Object>> res = new ArrayList<>();
			for (PrePaySupply s : lists) {

				HashMap<String, Object> m = new HashMap<>();
				m.put("type", s.getType());
				m.put("supplierElement", s.getSupplierElement().getName());
				m.put("money", s.getMoney());
				m.put("createDate", s.getCreateTime());
				m.put("operator", s.getOperator().getName());
				m.put("department", s.getDepartmentId().getFullName());
				m.put("state", s.getState());
				m.put("payTime", s.getPayTime());
				m.put("id", s.getId());

				res.add(m);
			}
			
			
			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", res.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", res.subList((page - 1) * rows, page * rows > res.size() ? res.size() : page * rows)); // 手动分页？
			json.setSuccess(true);
			if (res.size() == 0) {
				json.setMsg("未获取到符合条件的数据");
			} else
				json.setMsg("查询成功");
			
			json.setObj(answer);
			json.setSuccess(true);
			
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("查询失败");
		}
		return json;
	}

	
	
	
	/**
	 * 预付款申请-详情页
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
				throw new Exception("id错误，找不到该条数据");
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
			obj.put("supplierElementId",prePaySupply.getId());
			obj.put("bankName", prePaySupply.getBankAccount().getBankName());
			obj.put("bankAccountId",prePaySupply.getBankAccount().getId());
			obj.put("accountName", prePaySupply.getBankAccount().getAccountName());
			obj.put("bankAccount", prePaySupply.getBankAccount().getBankAccount());
			obj.put("bankCode", prePaySupply.getBankAccount().getBankCode());
			obj.put("bankType", prePaySupply.getBankAccount().getBankType());
			obj.put("id", id);
			
			obj.put("money", prePaySupply.getMoney());
			obj.put("memo", prePaySupply.getMemo());
			
			json.setObj(obj);
			
			json.setSuccess(true);
			json.setMsg("操作成功");
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败:"+e.getMessage());
		}
		return json;
	}

	/**
	 * 输入供应商类型，返回供应商列表 type从0开始
	 */
	@RequestMapping("/supplier/list")
	@ResponseBody
	public Json supplierList(Integer type) {
		Json json = new Json();
		try {

			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("supplierType", SupplierType.values()[type]));
			List<HySupplierElement> lists = hySupplierElementService.findList(null, filters, null);
			List<HashMap<String, Object>> res = new ArrayList<>();
			for (HySupplierElement s : lists) {
				HashMap<String, Object> m = new HashMap<>();

				m.put("supplierElementName", s.getName());
				if(s.getBankList()!=null){
					m.put("bankName", s.getBankList().getBankName()); 
					m.put("accountName", s.getBankList().getAccountName());
					m.put("bankAccount", s.getBankList().getBankAccount());
					m.put("bankAccountId", s.getBankList().getId());
					m.put("bankCode", s.getBankList().getBankCode());
					m.put("bankType", s.getBankList().getBankType());
				}else{
					m.put("bankName", null);
					m.put("accountName", null);
					m.put("bankAccount", null);
					m.put("bankAccountId", null);
					m.put("bankCode", null);
					m.put("bankType", null);
				}
				
				m.put("supplierElementId", s.getId());

				res.add(m);
			}
			json.setObj(res);
			json.setSuccess(true);
			json.setMsg("获取成功");

		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("获取失败");

		}
		return json;
	}

	/**
	 * 提交申请，并将申请存入申请记录表
	 * 
	 * @param httpSession
	 * @param bankAccount
	 *            //id
	 * @param money
	 * @param memo
	 * @return
	 */
	@RequestMapping("/submit")
	@ResponseBody
	public Json prePaySupplySubmit(HttpSession httpSession,Long bankAccountId,String memo,
         BigDecimal money,Long supplierElementId) {
		Json json = new Json();
		try {
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
//			String treePath = hyAdminService.find(username).getDepartment().getTreePath();
			Long department = hyAdminService.find(username).getDepartment().getId();
//			String[] strings = treePath.split(",");
//			Long branchId = 0L;		
			Boolean isBranch = false;
			BigDecimal balance = new BigDecimal(0);

			HashMap<String, Object> map = new HashMap<>();
			HashMap<String, Object> map2 = new HashMap<>();
			
			Department company = EmployeeUtil.getCompany(hyAdminService.find(username));
			int step = -1; //审核步骤  4待部门经理审核 0：待经理超额审核 1：待副总超额审核 2：待财务审核 3：总公司财务审核完成 
			
			// 2019/3/1 改
			String role=hyAdminService.find(username).getRole().getName();
			boolean ifjingli = false;
			if(role.contains("经理")) {
				map2.put("ifjingli", "true");
				ifjingli = true;
			}else{
				map2.put("ifjingli", "false");
			}
			map2.put("department", department);
			if(company.getHyDepartmentModel().getName().equals("总公司")){
				map.put("startType", "company");
//				map2.put("departmentId", 1);
				
				if(!ifjingli){ //如果不是经理，则进入经理审核
					step = 4;
				} else {

					// 2019/2/28 改  by wangjie
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("eduleixing", Eduleixing.prepayCompany));
					List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
					BigDecimal money1 = edu.get(0).getMoney();
					if (money.compareTo(money1) > 0) { // 超过额度
						step = 0; // 待产品中心经理审核
						map2.put("money1", "more");
					} else {
						map2.put("money1", "less");
						filters.clear();
						filters.add(Filter.eq("eduleixing", Eduleixing.prePayLimit));
						List<CommonShenheedu> edu2 = commonEdushenheService.findList(null, filters, null);
						BigDecimal money2 = edu2.get(0).getMoney();
						if (money.compareTo(money2) > 0) { // 超过额度
							map2.put("money2", "more");
							step = 1;// 待副总限额审核
						} else {
							map2.put("money2", "less");
							step = 2; // 待财务审核
						}
					}
				}
				map2.put("departmentId", 1);
			}else{
				isBranch = true;
				map.put("startType", "branch");
//				map2.put("departmentId", company.getId());
				
				if(!ifjingli){//如果不是经理提交  进入经理审核
					step = 4;
				}else{
				//  2019/2/28 改  by wangjie
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("eduleixing", Eduleixing.prepayBranch));
					List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
					BigDecimal money1 = edu.get(0).getMoney();
					if(money.compareTo(money1)>0){  //超过额度
						step = 0;  //待产品中心经理审核
						map2.put("money1", "more");
					}else{
						map2.put("money1", "less");
						filters.clear();
						filters.add(Filter.eq("eduleixing", Eduleixing.prePayLimit));
						List<CommonShenheedu> edu2 = commonEdushenheService.findList(null, filters, null);
						BigDecimal money2 = edu2.get(0).getMoney();
						if(money.compareTo(money2)>0){  //超过额度
							map2.put("money2", "more");
							step = 1;//待副总限额审核
						}else{
							map2.put("money2", "less");
							step = 2; //待财务审核
						}
					}
				}		
				map2.put("departmentId", company.getId());
				
				List<Filter> fils = new ArrayList<>();
				fils.add(Filter.eq("branchId", company.getId()));
				List<BranchBalance> list = branchBalanceService.findList(null, fils, null);
				if(list.size()!=0&&list!=null){
					BranchBalance branchbalance = list.get(0);
					balance = branchbalance.getBranchBalance();
				}
			}
			
			
//			if (strings.length > 2) {
//				if (strings[2].equals("2") ) {
//					map.put("startType", "company");
//					map2.put("departmentId", 1);
//				} else {
//					isBranch = true;
//					map.put("startType", "branch");
//					branchId = Long.parseLong(strings[2]);
//					System.out.println("branchid:"+branchId);
//
//					List<Filter> fils = new ArrayList<>();
//					fils.add(Filter.eq("branchId", branchId));
//					List<BranchBalance> list = branchBalanceService.findList(null, fils, null);
//					BranchBalance branchbalance = list.get(0);
//
//					balance = branchbalance.getBranchBalance();
//					System.out.println("balance: "+balance);
//					
//					map2.put("departmentId", branchId);
//				}
//			} else {
//				map.put("startType", "company");
//				map2.put("departmentId", 1);
//			}

			// 如果总公司，不用判断是否申请资金和余额，如果是分公司，判断申请资金是否大于余额，如果大于则允许申请。
			boolean whetherStartProcess;
			if (isBranch && money.compareTo(balance) != 1) {
				whetherStartProcess = true;
			} else if (!isBranch) {
				whetherStartProcess = true;
			} else {
				whetherStartProcess = false;
			}

			if (whetherStartProcess) {
				ProcessInstance pi = runtimeService.startProcessInstanceByKey("prePay", map);
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
				taskService.claim(task.getId(),username);
				taskService.complete(task.getId(),map2);

				PrePaySupply prePaySupply = new PrePaySupply();
				prePaySupply.setBankAccount(bankListService.find(bankAccountId));
				prePaySupply.setDepartmentId(departmentService.find(department));
				if(memo == null){
					memo = "无";
				}
				prePaySupply.setMemo(memo);
				prePaySupply.setMoney(money);
				prePaySupply.setOperator(hyAdminService.find(username));
				prePaySupply.setProcessInstanceId(pi.getProcessInstanceId());
				prePaySupply.setState(0); // 待审核
				prePaySupply.setType(hySupplierElementService.find(supplierElementId).getSupplierType().ordinal());
				prePaySupply.setCreateTime(new Date());
				prePaySupply.setSupplierElement(hySupplierElementService.find(supplierElementId));
				prePaySupply.setStep(step);// 提交充值申请

				prePaySupplyService.save(prePaySupply);

				json.setMsg("提交成功");
			} else {
				json.setMsg("申请金额超过充值余额,提交失败");
			}
			json.setSuccess(true);

		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("提交失败");
		}
		return json;
	}
	
	
	
	
	/**编辑
	 * 
	 * @param prePaySupply
	 * @param httpSession
	 * @return
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public Json prePaySupplyEdit(HttpSession httpSession,Long bankAccountId,String memo,
	         BigDecimal money,Long supplierElementId,Long id){
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();
		try {

			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			String treePath = hyAdminService.find(username).getDepartment().getTreePath();
			Long department = hyAdminService.find(username).getDepartment().getId();
			String[] strings = treePath.split(",");
			Long branchId = 0L;
			Boolean isBranch = false;
			BigDecimal balance = new BigDecimal(0);

			HashMap<String, Object> map = new HashMap<>();
			HashMap<String, Object> map2 = new HashMap<>();
			if (strings.length > 2) {
				if (strings[2].equals("2") ) {
					map.put("startType", "company");
					map2.put("departmentId", 1);
				} else {
					isBranch = true;
					map.put("startType", "branch");
					branchId = Long.parseLong(strings[2]);
					System.out.println("branchid:"+branchId);

					List<Filter> fils = new ArrayList<>();
					fils.add(Filter.eq("branchId", branchId));
					List<BranchBalance> list = branchBalanceService.findList(null, fils, null);
					BranchBalance branchbalance = list.get(0);

					balance = branchbalance.getBranchBalance();
					System.out.println("balance: "+balance);
					
					map2.put("departmentId", branchId);
				}
			} else {
				map.put("startType", "company");
				map2.put("departmentId", 1);
			}

			// 如果总公司，不用判断是否申请资金和余额，如果是分公司，判断申请资金是否大于余额，如果大于则允许申请。
			boolean whetherStartProcess;
			if (isBranch && money.compareTo(balance) != 1) {
				whetherStartProcess = true;
			} else if (!isBranch) {
				whetherStartProcess = true;
			} else {
				whetherStartProcess = false;
			}

			if (whetherStartProcess) {
				ProcessInstance pi = runtimeService.startProcessInstanceByKey("prePay", map);
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
				taskService.claim( task.getId(),username);
				taskService.complete(task.getId(),map2);

				PrePaySupply prePaySupply = prePaySupplyService.find(id);
				prePaySupply.setBankAccount(bankListService.find(bankAccountId));
				prePaySupply.setDepartmentId(departmentService.find(department));
				if(memo == null){
					memo ="无";
				}
				prePaySupply.setMemo(memo);
				prePaySupply.setMoney(money);
				prePaySupply.setOperator(hyAdminService.find(username));
				prePaySupply.setProcessInstanceId(pi.getProcessInstanceId());
				prePaySupply.setState(0); // 待审核
				prePaySupply.setType(hySupplierElementService.find(supplierElementId).getSupplierType().ordinal());
				prePaySupply.setCreateTime(new Date());
				prePaySupply.setSupplierElement(hySupplierElementService.find(supplierElementId));
				prePaySupply.setStep(0);// 提交充值申请


				prePaySupplyService.update(prePaySupply);

				json.setMsg("提交成功");
			} else {
				json.setMsg("申请金额超过充值余额,提交失败");
			}
			json.setSuccess(true);

		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("提交失败");
		}
		return json;
	}
	
	
	

}
