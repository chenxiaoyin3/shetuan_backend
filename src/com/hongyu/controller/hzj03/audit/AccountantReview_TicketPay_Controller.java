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
import com.hongyu.entity.BankList;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyPaymentSupplier;
import com.hongyu.entity.PayServicer;
import com.hongyu.service.BankListService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDistributorManagementService;
import com.hongyu.service.HyDistributorPrechargeRecordService;
import com.hongyu.service.HyPaymentSupplierService;
import com.hongyu.service.HyPaymentpreJiangtaiService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.ReceiptDetailsService;
import com.hongyu.service.ReceiptDistributorRechargeService;

/** 审核 - 向酒店/门店/酒加景付款 */
@Controller
@RequestMapping("admin/ticketPay")
public class AccountantReview_TicketPay_Controller {
	
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;
	
	@Resource(name = "bankListServiceImpl")
	BankListService bankListService;

	@Resource(name = "hyPaymentSupplierServiceImpl")
	HyPaymentSupplierService hyPaymentSupplierService;

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

	/** 向酒店/门店/酒加景付款- 审核 - 列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json ticketPayReviewList(Pageable pageable, Integer state, HyPaymentSupplier hyPaymentSupplier,
			String startTime, String endTime, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(hyPaymentSupplier);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(new Filter("createDate", Operator.ge,
						sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(
						new Filter("createDate", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));

			List<HyPaymentSupplier> hyPaymentSuppliers = hyPaymentSupplierService.findList(null, filters, null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyPaymentSupplier tmp : hyPaymentSuppliers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);// 0未审核
							m.put("createDate", tmp.getCreateDate()); 
							m.put("supplierName", tmp.getPiaowubuGongyingshang().getSupplierName());
							m.put("applier", tmp.getOperator().getName());
							m.put("money", tmp.getMoney());
							ans.add(m);
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyPaymentSupplier tmp : hyPaymentSuppliers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);// 1已审核
							m.put("createDate", tmp.getCreateDate()); 
							m.put("supplierName", tmp.getPiaowubuGongyingshang().getSupplierName());
							m.put("applier", tmp.getOperator().getName());
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
					for (HyPaymentSupplier tmp : hyPaymentSuppliers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);// 0未审核
							m.put("createDate", tmp.getCreateDate()); 
							m.put("supplierName", tmp.getPiaowubuGongyingshang().getSupplierName());
							m.put("applier", tmp.getOperator().getName());
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
					for (HyPaymentSupplier tmp : hyPaymentSuppliers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);// 1已审核
							m.put("createDate", tmp.getCreateDate()); 
							m.put("supplierName",tmp.getPiaowubuGongyingshang().getSupplierName());
							m.put("applier", tmp.getOperator().getName());
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

	/** 向酒店/门店/酒加景付款- 审核 - 详情 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json getHistoryComments(Long id) {
		Json json = new Json();
		try {
			HyPaymentSupplier hyPaymentSupplier = hyPaymentSupplierService.find(id);
			String processInstanceId = hyPaymentSupplier.getProcessInstanceId();
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

			BankList bankList = hyPaymentSupplier.getPiaowubuGongyingshang().getBankId();
			
			// 分销商信息
			obj.put("accountName", bankList.getAccountName()); // 账户名称
			obj.put("bankName", bankList.getBankName()); // 银行名称
			obj.put("bankCode", bankList.getBankCode()); // 银行联行号
			obj.put("bankType", bankList.getBankType()); // 对公对私
			obj.put("bankAccount", bankList.getBankAccount()); // 帐号

			// 申请信息
			obj.put("supplierName", hyPaymentSupplier.getPiaowubuGongyingshang().getSupplierName());
			obj.put("createDate", hyPaymentSupplier.getCreateDate());
			obj.put("operator", hyPaymentSupplier.getOperator().getName());
			obj.put("startTime", hyPaymentSupplier.getStartTime());
			obj.put("endTime", hyPaymentSupplier.getEndTime());
			obj.put("money", hyPaymentSupplier.getMoney());

			json.setSuccess(true);
			json.setObj(obj);

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}
		return json;
	}

	/** 向酒店/门店/酒加景付款 - 审核 - 审核 */
	@RequestMapping("audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin admin = hyAdminService.find(username);
			HyPaymentSupplier hyPaymentSupplier = hyPaymentSupplierService.find(id);
			String processInstanceId = hyPaymentSupplier.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (state == 1) { // 审核通过
					if(hyPaymentSupplier.getCheckStatus() == 0){ // checkStatus为0时需要副总审核
						hyPaymentSupplier.setCheckStatus(1); //
					}else if(hyPaymentSupplier.getCheckStatus() == 1){ //checkStaus为1时需要财务审核
						hyPaymentSupplier.setCheckStatus(2);
						
						
						//审核通过后，在hy_pay_servicer表中增加数据
						PayServicer payServicer = new PayServicer();
						payServicer.setReviewId(id);
						payServicer.setHasPaid(0); // 0:未付  1:已付
						payServicer.setType(5); //1:预付款 2:T+N 3:提前打款 4:旅游元素供应商尾款 5:向酒店/门票/酒加景供应商付款 6:江泰预充值
						payServicer.setApplyDate(hyPaymentSupplier.getCreateDate());
						payServicer.setAppliName(hyPaymentSupplier.getOperator().getName());
						payServicer.setServicerId(hyPaymentSupplier.getPiaowubuGongyingshang().getId()); 
						payServicer.setServicerName(hyPaymentSupplier.getPiaowubuGongyingshang().getSupplierName());
						payServicer.setAmount(hyPaymentSupplier.getMoney());
						payServicer.setRemark(hyPaymentSupplier.getRemark());
						
						BankList bankList = hyPaymentSupplier.getPiaowubuGongyingshang().getBankId();
						
						payServicer.setBankListId(bankList.getId());  
						payServicer.setAccountName(bankList.getAccountName());
						payServicer.setBankName(bankList.getBankName());
						payServicer.setBankCode(bankList.getBankCode()); 
						payServicer.setBankType(bankList.getBankType() == false ? 1 : 0); //0:对私  1:对公  
						payServicer.setBankAccount(bankList.getBankAccount());
						
						payServicerService.save(payServicer);
						
					}
	

				} else if (state == 0) { // 审核驳回
					hyPaymentSupplier.setCheckStatus(3); // 3:驳回
					hyPaymentSupplierService.update(hyPaymentSupplier);
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? " " : comment + ":" + state);
				HashMap<String, Object> map = new HashMap<>();
				map.put("state", state);
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId(), map);

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
