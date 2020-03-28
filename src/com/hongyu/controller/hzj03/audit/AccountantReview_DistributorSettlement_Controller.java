package com.hongyu.controller.hzj03.audit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
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
import com.hongyu.entity.HyDistributorManagement;
import com.hongyu.entity.HyDistributorSettlement;
import com.hongyu.entity.ReceiptBillingCycle;
import com.hongyu.entity.ReceiptDetail;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDistributorManagementService;
import com.hongyu.service.HyDistributorPrechargeRecordService;
import com.hongyu.service.HyDistributorSettlementService;
import com.hongyu.service.HyPaymentpreJiangtaiService;
import com.hongyu.service.ReceiptBillingCycleService;
import com.hongyu.service.ReceiptDetailsService;
import com.hongyu.service.ReceiptDistributorRechargeService;

/** 财务 - 审核 - 分销商 结算 */
@Controller
@RequestMapping("admin/accountant/distributorSettlement")
public class AccountantReview_DistributorSettlement_Controller {
	
	/**待收款-已收*/
	private static final int RECEIVED = 1;
	@Resource(name = "receiptBillingCycleServiceImpl")
	ReceiptBillingCycleService receiptBillingCycleService;
	
	@Resource(name = "receiptDistributorRechargeServiceImpl")
	ReceiptDistributorRechargeService receiptDistributorRechargeService;
	
	@Resource(name = "hyDistributorManagementServiceImpl")
	HyDistributorManagementService hyDistributorManagementService;

	@Resource(name = "hyDistributorSettlementServiceImpl")
	HyDistributorSettlementService hyDistributorSettlementService;
	
	@Resource(name = "hyDistributorPrechargeRecordServiceImpl")
	HyDistributorPrechargeRecordService hyDistributorPrechargeRecordService;
	
	@Resource(name = "hyPaymentpreJiangtaiServiceImpl")
	HyPaymentpreJiangtaiService hyPaymentpreJiangtaiService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "receiptDetailsServiceImpl")
	ReceiptDetailsService receiptDetailsService;

	@Resource
	private TaskService taskService;
	
	@Resource
	private HistoryService historyService;


	/** 分销商 结算 审核 - 列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json distributorSettlementReviewList(Pageable pageable, Integer state, HyDistributorSettlement hyDistributorSettlement,
			String startTime, String endTime, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(hyDistributorSettlement);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(new Filter("payDate", Operator.ge,
						sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(
						new Filter("payDate", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			
			List<HyDistributorSettlement> hyDistributorSettlements = hyDistributorSettlementService.findList(null, filters,
					null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyDistributorSettlement tmp : hyDistributorSettlements) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
									
							m.put("id", tmp.getId());
							m.put("state", 0);// 0未审核
							m.put("money", tmp.getMoney());
							m.put("startDate", tmp.getStartDate());
							m.put("endDate", tmp.getEndDate());
							m.put("payDate", tmp.getPayDate());
							m.put("operator", tmp.getOperator().getName());
							
							ans.add(m);
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyDistributorSettlement tmp : hyDistributorSettlements) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);// 1未审核
							m.put("money", tmp.getMoney());
							m.put("startDate", tmp.getStartDate());
							m.put("endDate", tmp.getEndDate());
							m.put("payDate", tmp.getPayDate());
							m.put("operator", tmp.getOperator().getName());
							
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
					for (HyDistributorSettlement tmp : hyDistributorSettlements) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							
							m.put("id", tmp.getId());
							m.put("state", 0);// 0未审核
							m.put("money", tmp.getMoney());
							m.put("startDate", tmp.getStartDate());
							m.put("endDate", tmp.getEndDate());
							m.put("payDate", tmp.getPayDate());
							m.put("operator", tmp.getOperator().getName());
							
							ans.add(m);
						}
					}
				}

			} else if (state == 1) {// 搜索已完成任务
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyDistributorSettlement tmp : hyDistributorSettlements) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							
							m.put("id", tmp.getId());
							m.put("state", 1);// 1未审核
							m.put("money", tmp.getMoney());
							m.put("startDate", tmp.getStartDate());
							m.put("endDate", tmp.getEndDate());
							m.put("payDate", tmp.getPayDate());
							m.put("operator", tmp.getOperator().getName());
							
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

	/** 分销商充值审核 - 详情*/
	@RequestMapping("detail/view")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json getHistoryComments(Long id) {
		Json json = new Json();
		try {
			HyDistributorSettlement hyDistributorSettlement = hyDistributorSettlementService.find(id);
			String processInstanceId = hyDistributorSettlement.getProcessInstanceId();
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
			
			// 银行信息
			obj.put("accountName", hyDistributorSettlement.getBankList().getAccountName()); //账户名称
			obj.put("bankName", hyDistributorSettlement.getBankList().getBankName()); // 银行名称
			obj.put("bankCode", hyDistributorSettlement.getBankList().getBankCode()); // 银行联行号
			obj.put("bankType", ""); // 对公对私
			obj.put("bankAccount", hyDistributorSettlement.getBankList().getBankAccount()); // 帐号
			
			// 申请信息
			obj.put("money", hyDistributorSettlement.getMoney());
			obj.put("startDate", hyDistributorSettlement.getStartDate());
			obj.put("endDate", hyDistributorSettlement.getEndDate());
			obj.put("operator", hyDistributorSettlement.getOperator().getName());
			obj.put("distributorName", hyDistributorSettlement.getDistributor().getName());
			obj.put("remark", hyDistributorSettlement.getRemark());
			
			json.setSuccess(true);
			json.setObj(obj);
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}
		return json;
	}

	/** 分销商充值审核 - 审核*/
	@RequestMapping("audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HyDistributorSettlement hyDistributorSettlement = hyDistributorSettlementService.find(id);
			String processInstanceId = hyDistributorSettlement.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (state == 1) { // 审核通过
					Integer settleType = hyDistributorSettlement.getDistributor().getSettleType(); 
					if(settleType == 1){ //预充值方式的周期结算  1-precharge,2-periodic
						// 生成扣款记录
						// 修改hy_distributor_settlement的状态
						hyDistributorSettlement.setCheckStatus(2);
						hyDistributorSettlement.setComment(comment);
						hyDistributorSettlement.setAuditor(admin);
						hyDistributorSettlement.setCheckTime(new Date());
						hyDistributorSettlement.setChargeBalance(hyDistributorSettlement.getChargeBalance().subtract(hyDistributorSettlement.getMoney()));
						
						hyDistributorSettlementService.save(hyDistributorSettlement);
						
						// 修改hy_distributor_management的状态
						HyDistributorManagement hyDistributorManagement = hyDistributorSettlement.getDistributor();
						hyDistributorManagement.setPrechargeBalance(hyDistributorManagement.getPrechargeBalance().subtract(hyDistributorSettlement.getMoney()));
						hyDistributorManagementService.save(hyDistributorManagement);
						
					}else{// 非预充值方式的周期结算 
						// 修改hy_distributor_settlement的状态
						hyDistributorSettlement.setCheckStatus(2);
						hyDistributorSettlement.setComment(comment);
						hyDistributorSettlement.setAuditor(admin);
						hyDistributorSettlement.setCheckTime(new Date());
						
						hyDistributorSettlementService.save(hyDistributorSettlement);
						
						// 生成已收款记录
						// 1.在hy_receipt_billing_cycle表中增加数据
						ReceiptBillingCycle receiptBillingCycle = new ReceiptBillingCycle();
						receiptBillingCycle.setState(RECEIVED); 
						receiptBillingCycle.setApplyDate(hyDistributorSettlement.getPayDate());
						receiptBillingCycle.setAppliName(hyDistributorSettlement.getOperator().getName());
						receiptBillingCycle.setDistributorName(hyDistributorSettlement.getDistributor().getName());
						receiptBillingCycle.setBillingCycleStart(hyDistributorSettlement.getStartDate());
						receiptBillingCycle.setBillingCycleEnd(hyDistributorSettlement.getEndDate());
						receiptBillingCycle.setAmount(hyDistributorSettlement.getMoney());
						receiptBillingCycle.setDate(new Date());
						
						receiptBillingCycleService.save(receiptBillingCycle);
						
						// 2.在hy_receipt_details表中增加数据
						ReceiptDetail receiptDetail = new ReceiptDetail();
						receiptDetail.setReceiptType(6); // 6:门票分销商周期结算 ReceiptBilliCycle
						receiptDetail.setReceiptId(receiptBillingCycle.getId());
						receiptDetail.setAmount(hyDistributorSettlement.getMoney());
						receiptDetail.setPayMethod(1L); // 转账     非预充值的方式的分销商周期结算   
						receiptDetail.setAccountName(hyDistributorSettlement.getBankList().getAccountName());
						receiptDetail.setShroffAccount(hyDistributorSettlement.getBankList().getBankAccount());
						receiptDetail.setBankName(hyDistributorSettlement.getBankList().getBankName());
						receiptDetail.setDate(hyDistributorSettlement.getPayDate());
						receiptDetail.setRemark(hyDistributorSettlement.getRemark());
						
						receiptDetailsService.save(receiptDetail);
						
					}
				} else if (state == 0) { // 审核驳回
					// 修改hy_distributor_settlement状态
					hyDistributorSettlement.setCheckStatus(3);  //1-checking,2-passed,3-reject
					hyDistributorSettlementService.update(hyDistributorSettlement);
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.claim(task.getId(), username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? " " : comment + ":" + state);
				taskService.complete(task.getId());
				
				json.setSuccess(true);
				json.setMsg("操作成功");
			}

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}
		return json;
	}
}
