package com.hongyu.controller.wj;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

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

import org.activiti.engine.HistoryService;
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
import com.hongyu.Order;
import com.hongyu.Pageable;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.BranchRecharge;
import com.hongyu.entity.BranchRechargeRecord;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.PayDepositBranch;
import com.hongyu.entity.PayDetailsBranch;
import com.hongyu.entity.ReceiptBranchRecharge;
import com.hongyu.entity.ReceiptDetail;
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
import com.hongyu.util.ActivitiUtils;

import javafx.print.JobSettings;
import oracle.net.aso.b;

/** 总公司财务 - 审核 - 分公司充值 */
@Transactional
@Controller
@RequestMapping("/admin/branch/review")
public class AccountantReview_BranchRecharge_Controller {
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

//					List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
					List<Task> tasks = ActivitiUtils.getTaskList(username, "branchRecharge");
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
//					List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//							.finished().taskAssignee(username).list();
					List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "branchRecharge");
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

//					Collections.sort(ans, new Comparator<Map<String, Object>>() {
//						@Override
//						public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//							long id1 = (long) o1.get("id");
//							long id2 = (long) o2.get("id");
//							return id2 > id1 ? 1 : -1;
//						}
//					});

				} else if (state == 0) {// 搜索未完成任务
//					List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
//							.desc().list();
					List<Task> tasks = ActivitiUtils.getTaskList(username, "branchRecharge");
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
//					List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//							.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username)
//							.list();
					List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "branchRecharge");
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
				
				Collections.sort(ans, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						Date date1 = (Date) o1.get("createDate");
						Date date2 = (Date) o2.get("createDate");
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

	/**
	 * 总公司财务进行审核
	 * 
	 * @param id
	 * @param comment
	 * @param state
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();
		try {
			branchRechargeService.branchRechargeAudit(id, comment, state, session);
			json.setSuccess(true);
			json.setMsg("审核成功");
		}catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败："+e.getMessage());
			e.printStackTrace();
		}
		return json;
		
		
	}
		
		
		
	
	/**
	 * 总公司财务进行审核
	 * 
	 * @param id
	 * @param comment
	 * @param state
	 * @param session
	 * @return
	 */
	/*@RequestMapping(value = "/audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			BranchRecharge branchRecharge = branchRechargeService.find(id);
			String processInstanceId = branchRecharge.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (state == 1) {
					System.out.println("*******************已修改********");
					// 总公司财务审核通过，直接生成已充值记录
					// 1、在hy_receipt_branch_recharge表中写数据
					ReceiptBranchRecharge receiptBranchRecharge = new ReceiptBranchRecharge();
					receiptBranchRecharge.setState(1);
					receiptBranchRecharge.setBranchName(
							hyAdminService.find(branchRecharge.getUsername()).getDepartment().getFullName());

					receiptBranchRecharge.setPayer(hyAdminService.find(branchRecharge.getUsername()).getName());
					receiptBranchRecharge.setAmount(branchRecharge.getAmount());
					receiptBranchRecharge.setDate(new Date());
					receiptBranchRecharge.setReceiver(username);
					receiptBranchRechargeService.save(receiptBranchRecharge);

					// 2、在hy_receipt_details表中写入数据
					ReceiptDetail receiptDetail = new ReceiptDetail();
					receiptDetail.setReceiptType(4); // 4:ReceiptBranchRecharge
					receiptDetail.setReceiptId(receiptBranchRecharge.getId());
					receiptDetail.setAmount(branchRecharge.getAmount());
					receiptDetail.setPayMethod((long) 1); // 付款方式 1:转账 2:支付宝
															// 3:微信支付 4:现金 5:预存款
															// 6:刷卡

					receiptDetail.setAccountName(branchRecharge.getAccountAlias());
					receiptDetail.setShroffAccount(branchRecharge.getBankAccount());
					receiptDetail.setBankName(branchRecharge.getBankName());
					receiptDetail.setDate(receiptBranchRecharge.getDate());

					receiptDetailService.save(receiptDetail);
					
					//4、修改分公司预存款余额
					List<Filter> filters2 = new  ArrayList<>();
			    	filters2.add(Filter.eq("branchId",branchRecharge.getBranchId()));
					List<BranchBalance> lists = branchBalanceService.findList(null,filters2,null);
			    	if(!lists.isEmpty()){  //如果当前分公司余额表有记录
			    		BranchBalance b = lists.get(0);//拿到当前分公司的余额
			    		b.setBranchBalance(b.getBranchBalance().add(branchRecharge.getAmount()));
			    		branchBalanceService.update(b);
			    	}else{
			    		BranchBalance br = new BranchBalance();
			    		br.setBranchId(branchRecharge.getBranchId());
			    		br.setBranchBalance(branchRecharge.getAmount());
			    		branchBalanceService.save(br);
			    	}
					
					
					//3、修改充值记录branchpresave表（冲抵记录）
					BranchPreSave branchPreSave = new BranchPreSave();
					branchPreSave.setBranchId(branchRecharge.getBranchId());
					branchPreSave.setBranchName(departmentService.find(branchRecharge.getBranchId()).getName());
					branchPreSave.setDate(branchRecharge.getCreateDate());
					branchPreSave.setDepartmentName(departmentService.find(branchRecharge.getDepartmentId()).getFullName());
					branchPreSave.setType(1);
					branchPreSave.setRemark(branchRecharge.getRemark());
					branchPreSave.setAmount(branchRecharge.getAmount());
					
					
					List<Filter> f = new  ArrayList<>();
			    	f.add(Filter.eq("branchId",branchRecharge.getBranchId()));
					List<BranchBalance> ls = branchBalanceService.findList(null,f,null);
					BranchBalance b = lists.get(0);//拿到当前分公司余额
		    		branchPreSave.setPreSaveBalance(branchRecharge.getAmount());
			    	branchPreSaveService.save(branchPreSave);
			    	
			    	//分公司收款记录
			    	BranchRechargeRecord branchRechargeRecord = new BranchRechargeRecord();
			    	branchRechargeRecord.setHasPaid(true);
			    	branchRechargeRecord.setPayDate(receiptBranchRecharge.getDate());
			    	branchRechargeRecord.setAppliName(branchRecharge.getUsername());
			    	branchRechargeRecord.setAmount(branchRecharge.getAmount());
			    	branchRechargeRecord.setRemark(branchRecharge.getRemark());
			    	branchRechargeRecord.setBranchId(branchRecharge.getBranchId());
			    	branchRechargeRecord.setBranchRechargeId(branchRecharge.getId());
			    	branchRechargeRecordService.save(branchRechargeRecord);
					
			    	PayDetailsBranch payDetailsBranch = new PayDetailsBranch();
			    	payDetailsBranch.setAccount(branchRecharge.getAccountAlias());
			    	payDetailsBranch.setAmount(branchRecharge.getAmount());
			    	payDetailsBranch.setDate(receiptBranchRecharge.getDate());
			    	payDetailsBranch.setOperator(username);
			    	payDetailsBranch.setPayId(branchRechargeRecord.getId());
			    	payDetailsBranch.setPayMethod((long)1);
			    	payDetailsBranch.setSort(1);
			    	payDetailsBranchService.save(payDetailsBranch);
	    	
						

//					// 修改分公司预存款余额
//					Long branchId = 0L;
//					String treePath = hyAdminService.find(branchRecharge.getUsername()).getDepartment().getTreePath();
//					String[] strings = treePath.split(",");
//
//					System.out.println("treepath :" + treePath);
//					System.out.println("len strings:" + strings.length);
//
//					if (strings.length > 2) {
//						branchId = Long.parseLong(strings[2]);
//					} else {
//						json.setMsg("该申请人不是分公司申请人");
//					}
//
//					List<Filter> filters = new ArrayList<>();
//					filters.add(Filter.eq("branchId", branchId));
//					List<BranchBalance> list = branchBalanceService.findList(null, filters, null);
//					BranchBalance branchbalance = list.get(0);
//					if (branchbalance != null) {
//						branchbalance
//								.setBranchBalance(branchbalance.getBranchBalance().add(branchRecharge.getAmount()));
//						branchBalanceService.update(branchbalance);
//					}
			    	branchRecharge.setStatus(2);

				} else if (state == 0) {
					branchRecharge.setStatus(2);
				}

				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? " " : comment + ":" + state);
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId());
				branchRechargeService.update(branchRecharge);
				json.setSuccess(true);
				json.setMsg("审核成功");
//			branchRechargeService.branchRechargeAudit(id, comment, state, session);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败："+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}*/
}
