package com.hongyu.controller.wj;

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
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.crypto.hash.Hash;
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
import com.hongyu.entity.ReceiptDetail;
import com.hongyu.entity.ReceiptStoreRecharge;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.StorePreSave;
import com.hongyu.entity.StorePreSave;
import com.hongyu.entity.StoreRecharge;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.ReceiptDetailsService;
import com.hongyu.service.ReceiptStoreRechargeService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreRechargeService;
import com.hongyu.service.StoreService;
import com.hongyu.util.ActivitiUtils;

/**  总公司财务 - 审核 - 门店充值 */
@Controller
@RequestMapping("/admin/accountant/storeRecharge")
public class AccountantReview_StoreRecharge_Controller {
	
	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;
	
	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "receiptDetailsServiceImpl")
	ReceiptDetailsService receiptDetailService;
	
	
	@Resource(name = "receiptStoreRechargeServiceImpl")
	ReceiptStoreRechargeService receiptStoreRechargeService;

	@Resource(name = "storeRechargeServiceImpl")
	StoreRechargeService storeRechargeService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;
	@Resource
	private RepositoryService repositoryService;

	/** 门店充值-审核-列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json storePreSaveReviewList(Pageable pageable, Integer state, String storeName,
			String startTime, String endTime, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			List<Filter> filters = FilterUtil.getInstance().getFilter(storeRecharge);
			List<Filter> filters =  new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(new Filter("payDay", Operator.ge,
						sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(
						new Filter("payDay", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
//			filters.add(Filter.eq("type", 0));  //0充值
			if(storeName!=null){
				List<Filter> storefilter = new ArrayList<>();
				storefilter.add(Filter.eq("storeName", storeName));
				List<Store> stores = storeService.findList(null,storefilter,null);
				if(stores==null){
					json.setMsg("获取失败，没有当前门店");
					json.setSuccess(false);
					return json;
				}
				filters.add(Filter.eq("store",stores.get(0)));
			}
			
			List<StoreRecharge> storeRecharges = storeRechargeService.findList(null, filters, null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
//				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				List<Task> tasks = ActivitiUtils.getTaskList(username, "storeRecharge");
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (StoreRecharge tmp : storeRecharges) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", tmp.getStatus());
							m.put("payDay", tmp.getPayDay());
							m.put("type", tmp.getType());
							m.put("applicant", tmp.getOperator().getUsername());
							m.put("money", tmp.getMoney());
							m.put("store", tmp.getStore().getStoreName());
							ans.add(m);
						}
					}
				}
//				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//						.finished().taskAssignee(username).list();
				List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "storeRecharge");
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (StoreRecharge tmp : storeRecharges) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", tmp.getStatus());
							m.put("payDay", tmp.getPayDay());
							m.put("type", tmp.getType());
							m.put("applicant", tmp.getOperator().getUsername());
							m.put("money", tmp.getMoney());
							m.put("store", tmp.getStore().getStoreName());
							ans.add(m);
						}
					}
				}

//				Collections.sort(ans, new Comparator<Map<String, Object>>() {
//					@Override
//					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//						long id1 = (long) o1.get("id");
//						long id2 = (long) o2.get("id");
//						return id2 > id1 ? 1 : -1;
//					}
//				});

			} else if (state == 0) {// 搜索未完成任务
//				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
//						.desc().list();
				List<Task> tasks = ActivitiUtils.getTaskList(username, "storeRecharge");
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (StoreRecharge tmp : storeRecharges) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", tmp.getStatus());
							m.put("payDay", tmp.getPayDay());
							m.put("type", tmp.getType());
							m.put("applicant", tmp.getOperator().getUsername());
							m.put("money", tmp.getMoney());
							m.put("store", tmp.getStore().getStoreName());
							ans.add(m);
						}
					}
				}

			} else if (state == 1) {// 搜索已完成任务  已通过任务
//				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//						.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();
				List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "storeRecharge");
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (StoreRecharge tmp : storeRecharges) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())&&tmp.getStatus()==1) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", tmp.getStatus());
							m.put("payDay", tmp.getPayDay());
							m.put("type", tmp.getType());
							m.put("applicant", tmp.getOperator().getUsername());
							m.put("money", tmp.getMoney());
							m.put("store", tmp.getStore().getStoreName());
							ans.add(m);
						}
					}
				}
			}else if (state == 2) {// 搜索已完成任务  已驳回任务
//				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//						.orderByHistoricTaskInstanceStartTime().desc().finished().taskCandidateUser(username).list();
				List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "storeRecharge");

				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (StoreRecharge tmp : storeRecharges) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())&&tmp.getStatus()==2) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", tmp.getStatus());
							m.put("payDay", tmp.getPayDay());
							m.put("type", tmp.getType());
							m.put("applicant", tmp.getOperator().getUsername());
							m.put("money", tmp.getMoney());
							m.put("store", tmp.getStore().getStoreName());
							ans.add(m);
						}
					}
				}
			}
			
			
			Collections.sort(ans, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Date date1 = (Date) o1.get("payDay");
					Date date2 = (Date) o2.get("payDay");
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

	/** 门店充值-审核- 详情 */
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			StoreRecharge storeRecharge = storeRechargeService.find(id);
			if(storeRecharge != null){
			
				String processInstanceId = storeRecharge.getProcessInstanceId();
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
	
				// 门店信息 供应商信息
//				obj.put("payerName", storeRecharge.getPayerName()); // 付款人
//				obj.put("payerBank", storeRecharge.getPayerBank()); // 付款银行
//				obj.put("payerAccount", storeRecharge.getPayerAccount()); // 付款人账户
//				obj.put("payerBankAccount", storeRecharge.getPayeeBankAccount()); // 付款人银行账号
//				
//				obj.put("payeeName", storeRecharge.getPayeeName()); // 收款人
//				obj.put("payeeBank", storeRecharge.getPayeeBank()); //收款银行
//				obj.put("payDay", storeRecharge.getPayDay());//付款日期
//				obj.put("applicant", storeRecharge.getOperator().getUsername());//创建人
//				obj.put("payeeAccount", storeRecharge.getPayeeAccount()); //收款人账户
//				obj.put("payeeBankAccount", storeRecharge.getPayeeBankAccount());//收款人银行号
//				obj.put("createDate", storeRecharge.getCreateDate());//创建日期
//		//		obj.put("type", storeRecharge.getType());  //0充值
//				obj.put("store", storeRecharge.getStore().getStoreName());  //门店名称
//		//		obj.put("status", storeRecharge.getStatus());  //审核状态
//				obj.put("payment", storeRecharge.getPayment());//付款方式
//				
				obj.put("store", storeRecharge.getStore().getStoreName());  //门店名称
				obj.put("applicant", storeRecharge.getOperator().getUsername());//创建人
				obj.put("money", storeRecharge.getMoney());
				
				List<Map<String, Object>> answer = new LinkedList<>();
				HashMap<String, Object> map = new HashMap<>();
				map.put("payment", storeRecharge.getPayment());//付款方式
				map.put("payeeAccount", storeRecharge.getPayeeAccount()); //收款人账户
				map.put("payeeName", storeRecharge.getPayeeName()); // 收款人
				map.put("payeeBank", storeRecharge.getPayeeBank()); //收款银行
				map.put("payDay", storeRecharge.getPayDay());//付款日期
				map.put("money", storeRecharge.getMoney());
//				map.put("payeeName", storeRecharge.getPayeeName());
				map.put("comment", storeRecharge.getRemark());
			
				answer.add(map);
				obj.put("payInfo", answer);
				
				
				
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

	/** 门店充值-审核 - 操作 */
	@RequestMapping("/audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			StoreRecharge storeRecharge = storeRechargeService.find(id);
			String processInstanceId = storeRecharge.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (state == 1) {
					storeRecharge.setStatus(1);
					storeRecharge.setRemark("财务通过门店充值申请");

					// 总公司财务审核通过，直接生成已充值记录
					// 1、在hy_receipt_store_recharge表中写数据
					ReceiptStoreRecharge receiptStoreRecharge = new ReceiptStoreRecharge();
					receiptStoreRecharge.setState(1);
					
					receiptStoreRecharge.setStoreName(storeRecharge.getStore().getStoreName());
					receiptStoreRecharge.setPayer(storeRecharge.getOperator().getName());
					receiptStoreRecharge.setAmount(storeRecharge.getMoney());
					receiptStoreRecharge.setDate(storeRecharge.getPayDay());
					receiptStoreRecharge.setReceiver(storeRecharge.getPayeeName());
					receiptStoreRechargeService.save(receiptStoreRecharge);

					// 2、在hy_receipt_details表中写入数据
					ReceiptDetail receiptDetail = new ReceiptDetail();
					receiptDetail.setReceiptType(3); // 3:ReceiptStoreRecharge
					receiptDetail.setReceiptId(receiptStoreRecharge.getId());
					receiptDetail.setAmount(storeRecharge.getMoney());
					receiptDetail.setPayMethod((long) 1); // 付款方式 1:转账 2:支付宝
															// 3:微信支付 4:现金 5:预存款
															// 6:刷卡

					receiptDetail.setAccountName(storeRecharge.getPayeeBankAccount());
					receiptDetail.setShroffAccount(storeRecharge.getPayeeAccount());
					receiptDetail.setBankName(storeRecharge.getPayeeBank());
					receiptDetail.setDate(storeRecharge.getPayDay());
					
					receiptDetailService.save(receiptDetail);
					
					// 3、修改门店预存款表      并发情况下的数据一致性！
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("store", storeRecharge.getStore()));
					List<StoreAccount> list = storeAccountService.findList(null, filters, null);
					if(list.size()!=0){
						StoreAccount storeAccount = list.get(0);
						storeAccount.setBalance(storeAccount.getBalance().add(storeRecharge.getMoney()));
						storeAccountService.update(storeAccount);
					}else{
						StoreAccount storeAccount = new StoreAccount();
						storeAccount.setStore(storeRecharge.getStore());
						storeAccount.setBalance(storeRecharge.getMoney());
						storeAccountService.save(storeAccount);
					}
					
					// 4、修改门店预存款记录表
					Long storeAccountLogId = storeRecharge.getStoreAccountLogId();
					StoreAccountLog storeAccountLog = storeAccountLogService.find(storeAccountLogId);
					storeAccountLog.setStatus(1);
					storeAccountLogService.update(storeAccountLog);
					
					// 5、修改 总公司-财务中心-门店预存款表
					StorePreSave storePreSave = new StorePreSave();
					storePreSave.setStoreId(storeRecharge.getStore().getId());
					storePreSave.setStoreName(storeRecharge.getStore().getStoreName());
					storePreSave.setType(1); ////1:门店充值 2:报名退款  3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款 10:酒店/酒加景销售 11:酒店/酒加景退款 12:门店后返 13:供应商驳回订单 14:
					storePreSave.setDate(storeRecharge.getPayDay());
					storePreSave.setAmount(storeRecharge.getMoney());
					storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters, null).get(0).getBalance());

					storePreSaveService.save(storePreSave); 
					
				} else if (state == 0) {
					storeRecharge.setStatus(2); // 2驳回
					storeRecharge.setRemark(comment);
					
					// 4、修改门店预存款记录表
					Long storeAccountLogId = storeRecharge.getStoreAccountLogId();
					StoreAccountLog storeAccountLog = storeAccountLogService.find(storeAccountLogId);
					storeAccountLog.setStatus(2);
					storeAccountLogService.update(storeAccountLog);
				}
				
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? " " : comment + ":" + state);
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId());
				storeRechargeService.update(storeRecharge);
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
