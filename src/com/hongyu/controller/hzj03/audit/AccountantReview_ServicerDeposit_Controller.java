package com.hongyu.controller.hzj03.audit;

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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
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
import com.hongyu.entity.DepositServicer;
import com.hongyu.entity.DepositSupplier;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.ReceiptDepositServicer;
import com.hongyu.entity.ReceiptDetail;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.DepositServicerService;
import com.hongyu.service.DepositSupplierService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.ReceiptDepositServicerService;
import com.hongyu.service.ReceiptDepositStoreService;
import com.hongyu.service.ReceiptDetailsService;
import com.hongyu.util.Constants.AuditStatus;

/** 财务 - 审核 - 供应商押金 */
@Controller
@RequestMapping("admin/accountant/servicerDeposit")
public class AccountantReview_ServicerDeposit_Controller {
//	/** 待收款 - 供应商押金 - 未收款 */
//	private static final int NOT_RECEIVED = 0;
	/** 待收款 - 供应商押金 - 已收款 */
	private static final int RECEIVED = 1;
	
	@Resource(name = "depositSupplierServiceImpl")
	DepositSupplierService depositSupplierService;

	@Resource(name = "receiptDetailsServiceImpl")
	ReceiptDetailsService receiptDetailsService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;

	@Resource(name = "receiptDepositStoreServiceImpl")
	ReceiptDepositStoreService reeceiptDepositStoreService;

	@Resource(name = "receiptDepositServicerServiceImpl")
	ReceiptDepositServicerService receiptDepositServicerService;

	@Resource(name = "depositServicerServiceImpl")
	DepositServicerService depositServicerService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;

	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;

	@Resource
	private RepositoryService repositoryService;

	@Resource
	private FormService formService;

	/** 供应商押金审核-列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json storePreSaveReviewList(Pageable pageable, Integer state, DepositServicer depositServicer,
			String startTime, String endTime, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(depositServicer);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(
						new Filter("applyTime", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(
						new Filter("applyTime", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			List<DepositServicer> depositServicers = depositServicerService.findList(null, filters, null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (DepositServicer tmp : depositServicers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);
							m.put("supplierName", tmp.getContractId().getHySupplier().getSupplierName());
							m.put("contractCode", tmp.getContractId().getContractCode());
							m.put("money", tmp.getPayAmount());
							m.put("applyName", tmp.getServicerName().getName());
							m.put("applyTime", tmp.getApplyTime());
							m.put("shenheStatus", 0);
							ans.add(m);
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (DepositServicer tmp : depositServicers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
							String str = "";
							for (Comment c : comment) {
								if (username.equals(c.getUserId())) {
									str = c.getFullMessage();
									break;
								}
							}
							
							if (str.contains("yitongguo")) {
								HashMap<String, Object> m = new HashMap<>();
								m.put("id", tmp.getId());
								m.put("state", 1);
								m.put("supplierName", tmp.getContractId().getHySupplier().getSupplierName());
								m.put("contractCode", tmp.getContractId().getContractCode());
								m.put("money", tmp.getPayAmount());
								m.put("applyName", tmp.getServicerName().getName());
								m.put("applyTime", tmp.getApplyTime());
								m.put("shenheStatus", 1); // 1: 已通过
								ans.add(m);
							}
							if (str.contains("yibohui")) {
								HashMap<String, Object> m = new HashMap<>();
								m.put("id", tmp.getId());
								m.put("state", 1);
								m.put("supplierName", tmp.getContractId().getHySupplier().getSupplierName());
								m.put("contractCode", tmp.getContractId().getContractCode());
								m.put("money", tmp.getPayAmount());
								m.put("applyName", tmp.getServicerName().getName());
								m.put("applyTime", tmp.getApplyTime());
								m.put("shenheStatus", 2); // 2: 已驳回
								ans.add(m);
							}
						}
					}
				}

			}

			else if (state == 1) {// 已审核
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();

				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (DepositServicer tmp : depositServicers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
							String str = "";
							for (Comment c : comment) {
								if (username.equals(c.getUserId())) {
									str = c.getFullMessage();
									break;
								}
							}
							
							if (str.contains("yitongguo")) {
								HashMap<String, Object> m = new HashMap<>();
								m.put("id", tmp.getId());
								m.put("state", 1);
								m.put("supplierName", tmp.getContractId().getHySupplier().getSupplierName());
								m.put("contractCode", tmp.getContractId().getContractCode());
								m.put("money", tmp.getPayAmount());
								m.put("applyName", tmp.getServicerName().getName());
								m.put("applyTime", tmp.getApplyTime());
								m.put("shenheStatus", 1); // 1: 已通过
								ans.add(m);
							}
							if (str.contains("yibohui")) {
								HashMap<String, Object> m = new HashMap<>();
								m.put("id", tmp.getId());
								m.put("state", 1);
								m.put("supplierName", tmp.getContractId().getHySupplier().getSupplierName());
								m.put("contractCode", tmp.getContractId().getContractCode());
								m.put("money", tmp.getPayAmount());
								m.put("applyName", tmp.getServicerName().getName());
								m.put("applyTime", tmp.getApplyTime());
								m.put("shenheStatus", 2); // 2: 已驳回
								ans.add(m);
							}
						}
					}
				}
			}

			else if (state == 0) {// 搜索未完成任务
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
						.desc().list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (DepositServicer tmp : depositServicers) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);
							m.put("supplierName", tmp.getContractId().getHySupplier().getSupplierName());
							m.put("contractCode", tmp.getContractId().getContractCode());
							m.put("money", tmp.getPayAmount());
							m.put("applyName", tmp.getServicerName().getName());
							m.put("applyTime", tmp.getApplyTime());
							m.put("shenheStatus", 0); // 0 待审核
							ans.add(m);
						}
					}
				}
			}

			// 倒序
			Collections.sort(ans, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					long id1 = (long) o1.get("id");
					long id2 = (long) o2.get("id");
					return id2 > id1 ? 1 : -1;
				}
			});

			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", ans.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows)); // 手动分页？
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

	/** 供应商押金 审核 - 详情 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id, HttpSession session) {
		Json json = new Json();
		try {
			DepositServicer depositServicer = depositServicerService.find(id);
			// 历史批注信息
			String processInstanceId = depositServicer.getProcessInstanceId();
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
				map.put("time", comment.getTime());
				
				String str = comment.getFullMessage();
				
				if(str.contains("yitongguo")){ //已通过
					if(str.contains(":")){
						str.replace(":", "");
					}
					map.put("comment", str.replace("yitongguo", "").trim());
					map.put("result", "通过");
					//新增 供应商交押金审核通过判断是否有正常合同设置hysupplier的isactive字段 add by gxz 
					HySupplierContract c = depositServicer.getContractId();
					if(c != null &&c.getContractStatus() == ContractStatus.zhengchang) {
						if(c.getHySupplier() != null) {
							HySupplier s = c.getHySupplier();
							s.setIsActive(true);
							hySupplierService.update(s);
						}
					}
					
				}else if(str.contains("yibohui")){//已驳回
					if(str.contains(":")){
						str.replace(":", "");
					}
					map.put("comment", str.replace("yibohui", "").trim());
					map.put("result", "未通过");
				}
				result.add(map);
			}

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("res", result);

			// 申请信息和银行帐号信息
			obj.put("id", depositServicer.getId());
			obj.put("supplierName", depositServicer.getContractId().getHySupplier().getSupplierName());
			obj.put("contractCode", depositServicer.getContractId().getContractCode());
			obj.put("money", depositServicer.getPayAmount());
			obj.put("applyName", depositServicer.getServicerName().getName());
			obj.put("applyTime", depositServicer.getApplyTime());
			obj.put("payTime", depositServicer.getPayTime());
			obj.put("phone", depositServicer.getContractId().getLiable().getMobile());
			obj.put("payMethod", depositServicer.getFkfs());   //这里只有一种？
			// 供应商
			obj.put("kaihuhang", depositServicer.getKaihuhang());
			obj.put("kaihuren", depositServicer.getKaihuren());
			obj.put("supplierBankAccount", depositServicer.getBankAccount());
			obj.put("lianhanghao", depositServicer.getLianhanghao());
			obj.put("supplierAccountType", depositServicer.getAccountType());
			// 总公司
			obj.put("bankName", depositServicer.getBankList().getBankName());
			obj.put("accountName", depositServicer.getBankList().getAccountName());
			obj.put("bankAccount", depositServicer.getBankList().getBankAccount());
			obj.put("bankCode", depositServicer.getBankList().getBankCode());
			obj.put("bankType", depositServicer.getBankList().getBankType());

			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}
		return json;
	}

	/** 财务审核 */
	@RequestMapping("audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, String state, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			DepositServicer depositServicer = depositServicerService.find(id);

			HySupplierContract con = depositServicer.getContractId();
			String processInstanceId = depositServicer.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (state.equals("yitongguo")) {
					
					//财务审核通过，缴纳押金状态变为已通过，合同状态变为正常或者未生效，根据当前日期和合同生效日期决定
					depositServicer.setAuditStatus(AuditStatus.pass);
					if(con != null) {
						con.setShouldpayDeposit(BigDecimal.ZERO);
						Date startTime = con.getStartDate();
						if(startTime.before(new Date())) {
							con.setContractStatus(ContractStatus.zhengchang);
						} else {
							con.setContractStatus(ContractStatus.weishengxiao);
						}
					}
					hySupplierContractService.update(con);
					
					// 供应商押金审核通过，直接生成已收款记录,并返回前台待收款记录的id
					// 1、在hy_receipt_deposit_store表中写数据
					ReceiptDepositServicer receiptDepositServicer = new ReceiptDepositServicer();
					receiptDepositServicer.setState(RECEIVED); //
					receiptDepositServicer
							.setServiceName(depositServicer.getContractId().getHySupplier().getSupplierName());
					receiptDepositServicer.setPayer(depositServicer.getServicerName().getName());
					receiptDepositServicer.setAmount(depositServicer.getPayAmount());
					receiptDepositServicer.setDate(depositServicer.getApplyTime());

					receiptDepositServicerService.save(receiptDepositServicer);

					// 2、在ReceiptDeatails表中写数据
					ReceiptDetail receiptDetail = new ReceiptDetail();
					receiptDetail.setReceiptType(2); // 2:供应商押金-收款
					receiptDetail.setReceiptId(receiptDepositServicer.getId());
					receiptDetail.setAmount(depositServicer.getPayAmount());
					receiptDetail.setPayMethod(1L); // 付款方式 1:转账 2:支付宝 3:微信支付
													// 4:现金 5:预存款 6:刷卡

					receiptDetail.setAccountName(depositServicer.getBankList().getAccountName());
					receiptDetail.setShroffAccount(depositServicer.getBankList().getBankAccount());
					receiptDetail.setBankName(depositServicer.getBankList().getBankName());
					receiptDetail.setDate(depositServicer.getApplyTime());

					receiptDetailsService.save(receiptDetail);
					
					
					// 3.财务中心-供应商保证金表  增加数据
					DepositSupplier depositSupplier = new DepositSupplier();
					depositSupplier.setSupplierName(receiptDepositServicer.getServiceName());
					depositSupplier.setContractCode(receiptDepositServicer.getContractCode());
					depositSupplier.setOweAmount(new BigDecimal("0")); // 供应商交纳押金    欠退金额为0
					depositSupplier.setContractStatus(1); //1:正常 2:变更续签 3:退出
					depositSupplier.setPayTime(receiptDepositServicer.getDate());  //申请日期直接作为交纳日期？
					depositSupplier.setAmount(receiptDepositServicer.getAmount());
//					depositSupplier.setRemark(receiptDepositServicer.getRemark()); //receiptDepositServicer中没有remark?
					depositSupplierService.save(depositSupplier);
					
					

					// 返回给前台的数据中加入receiptDepositStore的id
					HashMap<String, Object> obj = new HashMap<>();
					obj.put("id", receiptDepositServicer.getId());
					json.setObj(obj);

				} else if (state.equals("yibohui")) {
					depositServicer.setAuditStatus(AuditStatus.notpass);
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId,
						comment == null ? ("  " + ":" + state) : (comment + ":" + state));
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId());

				// 更新申请业务表的状态
				depositServicerService.update(depositServicer);
				json.setSuccess(true);
				json.setMsg("审核成功");
			}

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;

	}
}
