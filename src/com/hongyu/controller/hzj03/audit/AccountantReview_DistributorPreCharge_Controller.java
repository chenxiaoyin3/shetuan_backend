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
import com.hongyu.entity.HyDistributorPrechargeRecord;
import com.hongyu.entity.ReceiptDetail;
import com.hongyu.entity.ReceiptDistributorRecharge;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDistributorManagementService;
import com.hongyu.service.HyDistributorPrechargeRecordService;
import com.hongyu.service.HyPaymentpreJiangtaiService;
import com.hongyu.service.ReceiptDetailsService;
import com.hongyu.service.ReceiptDistributorRechargeService;

/** 财务 - 审核 - 分销商 充值 */
@Controller
@RequestMapping("admin/accountant/distributorPrecharge")
public class AccountantReview_DistributorPreCharge_Controller {
	
	/**待收款-已收*/
	private static final int RECEIVED = 1;
	
	@Resource(name = "receiptDistributorRechargeServiceImpl")
	ReceiptDistributorRechargeService receiptDistributorRechargeService;
	
	@Resource(name = "hyDistributorManagementServiceImpl")
	HyDistributorManagementService hyDistributorManagementService;

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


	/** 分销商充值审核 - 列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json distributorPreChargeReviewList(Pageable pageable, Integer state, HyDistributorPrechargeRecord hyDistributorPrechargeRecord,
			String startTime, String endTime, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(hyDistributorPrechargeRecord);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(new Filter("chargeDate", Operator.ge,
						sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(
						new Filter("chargeDate", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			
			List<HyDistributorPrechargeRecord> hyDistributorPrechargeRecords = hyDistributorPrechargeRecordService.findList(null, filters,
					null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyDistributorPrechargeRecord tmp : hyDistributorPrechargeRecords) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);// 0未审核
							m.put("chargeMoney", tmp.getChargeMoney());
							m.put("chargeDate", tmp.getChargeDate());
							m.put("operator", tmp.getOperator().getName());
							m.put("distributorName", tmp.getDistributor().getName());
							
							ans.add(m);
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyDistributorPrechargeRecord tmp : hyDistributorPrechargeRecords) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);// 1已审核
							m.put("chargeMoney", tmp.getChargeMoney());
							m.put("chargeDate", tmp.getChargeDate());
							m.put("operator", tmp.getOperator().getName());
							m.put("distributorName", tmp.getDistributor().getName());
							
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
					for (HyDistributorPrechargeRecord tmp : hyDistributorPrechargeRecords) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);// 0未审核
							m.put("chargeMoney", tmp.getChargeMoney());
							m.put("chargeDate", tmp.getChargeDate());
							m.put("operator", tmp.getOperator().getName());
							m.put("distributorName", tmp.getDistributor().getName());
							
							ans.add(m);
						}
					}
				}

			} else if (state == 1) {// 搜索已完成任务
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyDistributorPrechargeRecord tmp : hyDistributorPrechargeRecords) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);// 1已审核
							m.put("chargeMoney", tmp.getChargeMoney());
							m.put("chargeDate", tmp.getChargeDate());
							m.put("operator", tmp.getOperator().getName());
							m.put("distributorName", tmp.getDistributor().getName());
							
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
			HyDistributorPrechargeRecord hyDistributorPrechargeRecord = hyDistributorPrechargeRecordService.find(id);
			String processInstanceId = hyDistributorPrechargeRecord.getProcessInstanceId();
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
			
			// 分销商信息
			obj.put("accountName", hyDistributorPrechargeRecord.getBankList().getAccountName()); //账户名称
			obj.put("bankName", hyDistributorPrechargeRecord.getBankList().getBankName()); // 银行名称
			obj.put("bankCode", hyDistributorPrechargeRecord.getBankList().getBankCode()); // 银行联行号
			obj.put("bankType", ""); // 对公对私
			obj.put("bankAccount", hyDistributorPrechargeRecord.getBankList().getBankAccount()); // 帐号
			
			// 申请信息
			obj.put("chargeMoney", hyDistributorPrechargeRecord.getChargeMoney());
			obj.put("chargeDate", hyDistributorPrechargeRecord.getChargeDate());
			obj.put("operator", hyDistributorPrechargeRecord.getOperator().getName());
			obj.put("distributorName", hyDistributorPrechargeRecord.getDistributor().getName());
			obj.put("remark", hyDistributorPrechargeRecord.getRemark());
			
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
			HyDistributorPrechargeRecord hyDistributorPrechargeRecord = hyDistributorPrechargeRecordService.find(id);
			String processInstanceId = hyDistributorPrechargeRecord.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (state == 1) { // 审核通过
					// 修改hy_distributor_precharge_record状态
					hyDistributorPrechargeRecord.setCheckStatus(2);  //1-checking,2-passed,3-reject
					hyDistributorPrechargeRecord.setAuditor(admin);
					hyDistributorPrechargeRecord.setCheckDate(new Date());
					hyDistributorPrechargeRecord.setCheckComment(comment);
					hyDistributorPrechargeRecord.setBalance(hyDistributorPrechargeRecord.getBalance().add(hyDistributorPrechargeRecord.getChargeMoney()));
					hyDistributorPrechargeRecordService.update(hyDistributorPrechargeRecord);
					
					// 修改hy_distributor_management状态
					HyDistributorManagement hyDistributorManagement = hyDistributorPrechargeRecord.getDistributor();
					hyDistributorManagement.setPrechargeBalance(hyDistributorManagement.getPrechargeBalance().add(hyDistributorPrechargeRecord.getChargeMoney()));
					hyDistributorManagementService.update(hyDistributorManagement);
					
					// 生成已收款记录
					//1.在hy_receipt_distributor_recharge表中增加数据
					ReceiptDistributorRecharge receiptDistributorRecharge = new ReceiptDistributorRecharge();
					receiptDistributorRecharge.setState(RECEIVED); // 1:已收
					receiptDistributorRecharge.setDate(hyDistributorPrechargeRecord.getChargeDate());
					receiptDistributorRecharge.setAppliName(hyDistributorPrechargeRecord.getOperator().getName());
					receiptDistributorRecharge.setDistributorName(hyDistributorPrechargeRecord.getDistributor().getName());
					receiptDistributorRecharge.setAmount(hyDistributorPrechargeRecord.getChargeMoney());
					receiptDistributorRecharge.setDate(hyDistributorPrechargeRecord.getChargeDate());
					
					receiptDistributorRechargeService.save(receiptDistributorRecharge);
					
					//2.在hy_receipt_details表中增加数据
					ReceiptDetail receiptDetail = new ReceiptDetail();
					receiptDetail.setReceiptType(5); // 5:门票分销商充值 ReceiptDistributorRecharge
					receiptDetail.setReceiptId(receiptDistributorRecharge.getId());
					receiptDetail.setAmount(hyDistributorPrechargeRecord.getChargeMoney());
					if(hyDistributorPrechargeRecord.getChargeType() == 1){ // 现金
						receiptDetail.setPayMethod(4L);
					}else if(hyDistributorPrechargeRecord.getChargeType() == 2){ // 刷卡
						receiptDetail.setPayMethod(6L);
					}else if(hyDistributorPrechargeRecord.getChargeType() == 3){ // 转账
						receiptDetail.setPayMethod(1L);
					}
					receiptDetail.setAccountName(hyDistributorPrechargeRecord.getBankList().getAccountName());
					receiptDetail.setShroffAccount(hyDistributorPrechargeRecord.getBankList().getBankAccount());
					receiptDetail.setBankName(hyDistributorPrechargeRecord.getBankList().getBankName());
					receiptDetail.setDate(hyDistributorPrechargeRecord.getChargeDate());
					receiptDetail.setRemark(hyDistributorPrechargeRecord.getRemark());
					
					receiptDetailsService.save(receiptDetail);
					
				} else if (state == 0) { // 审核驳回
					// 修改hy_distributor_precharge_record状态
					hyDistributorPrechargeRecord.setCheckStatus(3);  //1-checking,2-passed,3-reject
					hyDistributorPrechargeRecordService.update(hyDistributorPrechargeRecord);
					
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? " " : comment + ":" + state);
				taskService.claim(task.getId(), username);
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
