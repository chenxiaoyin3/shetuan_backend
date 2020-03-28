package com.hongyu.controller.gsbing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BalanceDueApplyItem;
import com.hongyu.entity.BankList;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.PaySettlement;
import com.hongyu.entity.PayablesBranchsettle;
import com.hongyu.entity.RegulategroupAccount;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.Department;
import com.hongyu.service.BalanceDueApplyItemService;
import com.hongyu.service.BankListService;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.PaySettlementService;
import com.hongyu.service.PayablesBranchsettleService;
import com.hongyu.service.RegulategroupAccountService;
import com.hongyu.util.ActivitiUtils;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants.DeductLine;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.util.liyang.EmployeeUtil;

@Controller
@RequestMapping("admin/branchsettle/review")
public class Review_branchsettleController {
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name = "paySettlementServiceImpl")
	PaySettlementService paySettlementService;
	
	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@Resource(name = "bankListServiceImpl")
	BankListService bankListService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	CommonEdushenheService commonEdushenheService;
	
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "balanceDueApplyItemServiceImpl")
	BalanceDueApplyItemService balanceDueApplyItemService;
	
	@Resource(name = "regulategroupAccountServiceImpl")
	RegulategroupAccountService regulategroupAccountService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name = "payablesBranchsettleServiceImpl")
	PayablesBranchsettleService payablesBranchsettleService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable, Integer state, String supplierName,String productId,
			String applyName,HttpSession session)
	{
		Json json=new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);	
			if(productId==null && supplierName==null){
				List<Filter> settleFilter=new ArrayList<>();
				if(applyName!=null&&!applyName.equals("")){
					Map<String, Object> answer = new HashMap<>();
					List<Map<String, Object>> ans = new ArrayList<>();
					List<Filter> filter=new ArrayList<Filter>();
					filter.add(Filter.like("name",applyName));
					List<HyAdmin> adminList=hyAdminService.findList(null,filter,null);
					if(adminList.size()==0){
						json.setMsg("查询成功");
					    json.setSuccess(true);
					    json.setObj(new Page<HyGroup>());
					}
					else{
						settleFilter.add(Filter.in("applyName", adminList));
						List<PayablesBranchsettle> settleList=payablesBranchsettleService.findList(null,settleFilter,null);
						if(state==null){
							HashSet<Long> idSet = new HashSet<>(); // 存放已经添加的申请业务表的id
//							List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
							List<Task> tasks = ActivitiUtils.getTaskList(username, "branchsettleProcess");
							for (Task task : tasks) {
								String processInstanceId = task.getProcessInstanceId();
								for (PayablesBranchsettle tmp : settleList) {
									HyGroup hyGroup=tmp.getHyGroup();
									if (processInstanceId.equals(tmp.getProcessInstanceId())) {
										HashMap<String, Object> m = new HashMap<>();
										m.put("id", tmp.getId());
										m.put("state", 0);
										m.put("productId", hyGroup.getLine().getPn());
										m.put("startDate", hyGroup.getStartDay());
										m.put("productName",  hyGroup.getLine().getName());
										m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
										m.put("money", tmp.getShifuMoney());
										m.put("applyDate", tmp.getApplyTime());
										m.put("applyName", tmp.getApplyName().getName());
										ans.add(m);
										idSet.add(tmp.getId());
									}
								}
							}
//							List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//									.finished().taskAssignee(username).list();
							List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "branchsettleProcess");
							for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
								String processInstanceId = historicTaskInstance.getProcessInstanceId();
								for (PayablesBranchsettle tmp : settleList) {
									HyGroup hyGroup=tmp.getHyGroup();
									if (processInstanceId.equals(tmp.getProcessInstanceId())) {
										if(idSet.add(tmp.getId())){
											HashMap<String, Object> m = new HashMap<>();
											m.put("id", tmp.getId());
											m.put("state", 1);
											m.put("productId", hyGroup.getLine().getPn());
											m.put("startDate", hyGroup.getStartDay());
											m.put("productName",  hyGroup.getLine().getName());
											m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
											m.put("money", tmp.getShifuMoney());
											m.put("applyDate", tmp.getApplyTime());
											m.put("applyName", tmp.getApplyName().getName());
											ans.add(m);
										}
									}
								}
							}
							Collections.sort(ans, new Comparator<Map<String, Object>>() {
								@Override
								public int compare(Map<String, Object> o1, Map<String, Object> o2) {
									Date date1 = (Date) o1.get("startDate");
									Date date2 = (Date) o2.get("startDate");
									return date1.compareTo(date2); 
								}
							});
						    Collections.reverse(ans);											
						}
						/*搜索未完成任务*/
						else if(state==0){
//							List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
//									.desc().list();
							List<Task> tasks = ActivitiUtils.getTaskList(username, "branchsettleProcess");
							for (Task task : tasks) {
								String processInstanceId = task.getProcessInstanceId();
								for (PayablesBranchsettle tmp : settleList) {
									HyGroup hyGroup=tmp.getHyGroup();
									if (processInstanceId.equals(tmp.getProcessInstanceId())) {
										HashMap<String, Object> m = new HashMap<>();
										m.put("id", tmp.getId());
										m.put("state", 0);
										m.put("productId", hyGroup.getLine().getPn());
										m.put("startDate", hyGroup.getStartDay());
										m.put("productName",  hyGroup.getLine().getName());
										m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
										m.put("money", tmp.getShifuMoney());
										m.put("applyDate", tmp.getApplyTime());
										m.put("applyName", tmp.getApplyName().getName());
										ans.add(m);
									}
								}
							}	
							Collections.sort(ans, new Comparator<Map<String, Object>>() {
								@Override
								public int compare(Map<String, Object> o1, Map<String, Object> o2) {
									Date date1 = (Date) o1.get("startDate");
									Date date2 = (Date) o2.get("startDate");
									return date1.compareTo(date2); 
								}
							});
						    Collections.reverse(ans);	
						}
						/*搜索已完成任务*/
						else if(state==1){
							// 搜索已完成任务
//							List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//									.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();
							List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "branchsettleProcess");
							for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
								String processInstanceId = historicTaskInstance.getProcessInstanceId();
								for (PayablesBranchsettle tmp : settleList) {
									HyGroup hyGroup=tmp.getHyGroup();
									if (processInstanceId.equals(tmp.getProcessInstanceId())) {
										HashMap<String, Object> m = new HashMap<>();
										m.put("id", tmp.getId());
										m.put("state", 1);
										m.put("productId", hyGroup.getLine().getPn());
										m.put("startDate", hyGroup.getStartDay());
										m.put("productName",  hyGroup.getLine().getName());
										m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
										m.put("money", tmp.getShifuMoney());
										m.put("applyDate", tmp.getApplyTime());
										m.put("applyName", tmp.getApplyName().getName());
										ans.add(m);
									}
								}
							}
							Collections.sort(ans, new Comparator<Map<String, Object>>() {
								@Override
								public int compare(Map<String, Object> o1, Map<String, Object> o2) {
									Date date1 = (Date) o1.get("startDate");
									Date date2 = (Date) o2.get("startDate");
									return date1.compareTo(date2); 
								}
							});
						    Collections.reverse(ans);	
						}
						int page = pageable.getPage();
						int rows = pageable.getRows();
						answer.put("total", ans.size());
						answer.put("pageNumber", page);
						answer.put("pageSize", rows);
						answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows)); // 手动分页？
						json.setSuccess(true);
						if (ans.size() == 0) {
							json.setMsg("未获取到符合条件的数据");
						} 
						else
							json.setMsg("获取成功");
						json.setObj(answer);
						return json;
					}
				}
				/*if(applyName==null)*/
				else{
					List<PayablesBranchsettle> settleList=payablesBranchsettleService.findAll();
					Map<String, Object> answer = new HashMap<>();
					List<Map<String, Object>> ans = new ArrayList<>();
					if(state==null){
						HashSet<Long> idSet = new HashSet<>(); // 存放已经添加的申请业务表的id
//						List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
						List<Task> tasks = ActivitiUtils.getTaskList(username, "branchsettleProcess");
						for (Task task : tasks) {
							String processInstanceId = task.getProcessInstanceId();
							for (PayablesBranchsettle tmp : settleList) {
								HyGroup hyGroup=tmp.getHyGroup();
								if (processInstanceId.equals(tmp.getProcessInstanceId())) {
									HashMap<String, Object> m = new HashMap<>();
									m.put("id", tmp.getId());
									m.put("state", 0);
									m.put("productId", hyGroup.getLine().getPn());
									m.put("startDate", hyGroup.getStartDay());
									m.put("productName",  hyGroup.getLine().getName());
									m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
									m.put("money", tmp.getShifuMoney());
									m.put("applyDate", tmp.getApplyTime());
									m.put("applyName", tmp.getApplyName().getName());
									ans.add(m);
									idSet.add(tmp.getId());
								}
							}
						}
//						List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//								.finished().taskAssignee(username).list();
						List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "branchsettleProcess");
						for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
							String processInstanceId = historicTaskInstance.getProcessInstanceId();
							for (PayablesBranchsettle tmp : settleList) {
								HyGroup hyGroup=tmp.getHyGroup();
								if (processInstanceId.equals(tmp.getProcessInstanceId())) {
									if(idSet.add(tmp.getId())){
										HashMap<String, Object> m = new HashMap<>();
										m.put("processId", processInstanceId);
										m.put("id", tmp.getId());
										m.put("state", 1);
										m.put("productId", hyGroup.getLine().getPn());
										m.put("startDate", hyGroup.getStartDay());
										m.put("productName",  hyGroup.getLine().getName());
										m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
										m.put("money", tmp.getShifuMoney());
										m.put("applyDate", tmp.getApplyTime());
										m.put("applyName", tmp.getApplyName().getName());
										ans.add(m);
									}
								}
							}
						}
						Collections.sort(ans, new Comparator<Map<String, Object>>() {
							@Override
							public int compare(Map<String, Object> o1, Map<String, Object> o2) {
								Date date1 = (Date) o1.get("startDate");
								Date date2 = (Date) o2.get("startDate");
								return date1.compareTo(date2); 
							}
						});
					    Collections.reverse(ans);											
					}
					/*搜索未完成任务*/
					else if(state==0){
//						List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
//								.desc().list();
						List<Task> tasks = ActivitiUtils.getTaskList(username, "branchsettleProcess");
						for (Task task : tasks) {
							String processInstanceId = task.getProcessInstanceId();
							for (PayablesBranchsettle tmp : settleList) {
								HyGroup hyGroup=tmp.getHyGroup();
								if (processInstanceId.equals(tmp.getProcessInstanceId())) {
									HashMap<String, Object> m = new HashMap<>();
									m.put("id", tmp.getId());
									m.put("state", 0);
									m.put("productId", hyGroup.getLine().getPn());
									m.put("startDate", hyGroup.getStartDay());
									m.put("productName",  hyGroup.getLine().getName());
									m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
									m.put("money", tmp.getShifuMoney());
									m.put("applyDate", tmp.getApplyTime());
									m.put("applyName", tmp.getApplyName().getName());
									ans.add(m);
								}
							}
						}	
						Collections.sort(ans, new Comparator<Map<String, Object>>() {
							@Override
							public int compare(Map<String, Object> o1, Map<String, Object> o2) {
								Date date1 = (Date) o1.get("startDate");
								Date date2 = (Date) o2.get("startDate");
								return date1.compareTo(date2); 
							}
						});
					    Collections.reverse(ans);	
					}
					/*搜索已完成任务*/
					else if(state==1){
						// 搜索已完成任务
//						List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//								.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();
						List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "branchsettleProcess");
						HashSet<Long> idSet = new HashSet<>(); // 存放已经添加的申请业务表的id
						for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
							String processInstanceId = historicTaskInstance.getProcessInstanceId();
							for (PayablesBranchsettle tmp : settleList) {
								HyGroup hyGroup=tmp.getHyGroup();
								if (processInstanceId.equals(tmp.getProcessInstanceId())) {
									HashMap<String, Object> m = new HashMap<>();
									m.put("id", tmp.getId());
									m.put("state", 1);
									m.put("productId", hyGroup.getLine().getPn());
									m.put("startDate", hyGroup.getStartDay());
									m.put("productName",  hyGroup.getLine().getName());
									m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
									m.put("money", tmp.getShifuMoney());
									m.put("applyDate", tmp.getApplyTime());
									m.put("applyName", tmp.getApplyName().getName());
									ans.add(m);
								}
							}
						}
						Collections.sort(ans, new Comparator<Map<String, Object>>() {
							@Override
							public int compare(Map<String, Object> o1, Map<String, Object> o2) {
								Date date1 = (Date) o1.get("startDate");
								Date date2 = (Date) o2.get("startDate");
								return date1.compareTo(date2); 
							}
						});
					    Collections.reverse(ans);	
					}
					int page = pageable.getPage();
					int rows = pageable.getRows();
					answer.put("total", ans.size());
					answer.put("pageNumber", page);
					answer.put("pageSize", rows);
					answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows)); // 手动分页？
					json.setSuccess(true);
					if (ans.size() == 0) {
						json.setMsg("未获取到符合条件的数据");
					} 
					else
						json.setMsg("获取成功");
					json.setObj(answer);
					return json;
				}
			}
			/*if(productId!=null || supplierName!=null)*/
			else{
				List<Filter> lineFilter=new ArrayList<Filter>();
				if(productId!=null && !productId.equals("")){
					lineFilter.add(Filter.eq("pn", productId));
				}
				if(supplierName!=null && !supplierName.equals("")){
					List<Filter> supplierFilter=new ArrayList<Filter>();
					supplierFilter.add(Filter.like("supplierName", supplierName));
					List<HySupplier> hySupplierList=hySupplierService.findList(null,supplierFilter,null);
					if(hySupplierList.size()==0){
						json.setSuccess(true);
						json.setMsg("查询成功");
						json.setObj(new Page<HySupplier>());
					}
					else{
						lineFilter.add(Filter.in("hySupplier",hySupplierList));
					}
				}
				List<HyLine> lineList=hyLineService.findList(null,lineFilter,null);
				if(lineList.size()==0){
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new Page<HyLine>());
				}
				else{
					List<Filter> groupFilter=new ArrayList<>();
					groupFilter.add(Filter.in("line", lineList));
					List<HyGroup> groupList=hyGroupService.findList(null,groupFilter,null);
					if(groupList.isEmpty()){
						json.setSuccess(true);
						json.setMsg("查询成功");
						json.setObj(new Page<HyGroup>());
				    }
					else{
						List<Filter> settleFilter=new ArrayList<>();
						settleFilter.add(Filter.in("hyGroup", groupList));
						if(applyName!=null){
							List<Filter> filter=new ArrayList<>();
							filter.add(Filter.like("name", applyName));
							List<HyAdmin> adminList=hyAdminService.findList(null,filter,null);
							if(adminList.isEmpty()){
								json.setSuccess(true);
								json.setMsg("查询成功");
								json.setObj(new Page<HyGroup>());
							}
							else{
								settleFilter.add(Filter.in("applyName", adminList));
								List<PayablesBranchsettle> settleList=payablesBranchsettleService.findList(null,settleFilter,null);
								if(settleList.isEmpty()){
									json.setSuccess(true);
									json.setMsg("查询成功");
									json.setObj(new Page<HyGroup>());
								}
								else{
									Map<String, Object> answer = new HashMap<>();
									List<Map<String, Object>> ans = new ArrayList<>();
									if(state==null){
										HashSet<Long> idSet = new HashSet<>(); // 存放已经添加的申请业务表的id
//										List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
										List<Task> tasks = ActivitiUtils.getTaskList(username, "branchsettleProcess");
										for (Task task : tasks) {
											String processInstanceId = task.getProcessInstanceId();
											for (PayablesBranchsettle tmp : settleList) {
												HyGroup hyGroup=tmp.getHyGroup();
												if (processInstanceId.equals(tmp.getProcessInstanceId())) {
													HashMap<String, Object> m = new HashMap<>();
													m.put("id", tmp.getId());
													m.put("state", 0);
													m.put("productId", hyGroup.getLine().getPn());
													m.put("startDate", hyGroup.getStartDay());
													m.put("productName",  hyGroup.getLine().getName());
													m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
													m.put("money", tmp.getShifuMoney());
													m.put("applyDate", tmp.getApplyTime());
													m.put("applyName", tmp.getApplyName().getName());
													ans.add(m);
													idSet.add(tmp.getId());
												}
											}
										}
//										List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//												.finished().taskAssignee(username).list();
										List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "branchsettleProcess");
										for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
											String processInstanceId = historicTaskInstance.getProcessInstanceId();
											for (PayablesBranchsettle tmp : settleList) {
												HyGroup hyGroup=tmp.getHyGroup();
												if (processInstanceId.equals(tmp.getProcessInstanceId())) {
													if(idSet.add(tmp.getId())){
														HashMap<String, Object> m = new HashMap<>();
														m.put("id", tmp.getId());
														m.put("state", 1);
														m.put("productId", hyGroup.getLine().getPn());
														m.put("startDate", hyGroup.getStartDay());
														m.put("productName",  hyGroup.getLine().getName());
														m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
														m.put("money", tmp.getShifuMoney());
														m.put("applyDate", tmp.getApplyTime());
														m.put("applyName", tmp.getApplyName().getName());
														ans.add(m);
													}
												}
											}
										}
										Collections.sort(ans, new Comparator<Map<String, Object>>() {
											@Override
											public int compare(Map<String, Object> o1, Map<String, Object> o2) {
												Date date1 = (Date) o1.get("startDate");
												Date date2 = (Date) o2.get("startDate");
												return date1.compareTo(date2); 
											}
										});
									    Collections.reverse(ans);											
									}
									/*搜索未完成任务*/
									else if(state==0){
//										List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
//												.desc().list();
										List<Task> tasks = ActivitiUtils.getTaskList(username, "branchsettleProcess");
										for (Task task : tasks) {
											String processInstanceId = task.getProcessInstanceId();
											for (PayablesBranchsettle tmp : settleList) {
												HyGroup hyGroup=tmp.getHyGroup();
												if (processInstanceId.equals(tmp.getProcessInstanceId())) {
													HashMap<String, Object> m = new HashMap<>();
													m.put("id", tmp.getId());
													m.put("state", 0);
													m.put("productId", hyGroup.getLine().getPn());
													m.put("startDate", hyGroup.getStartDay());
													m.put("productName",  hyGroup.getLine().getName());
													m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
													m.put("money", tmp.getShifuMoney());
													m.put("applyDate", tmp.getApplyTime());
													m.put("applyName", tmp.getApplyName().getName());
													ans.add(m);
												}
											}
										}	
										Collections.sort(ans, new Comparator<Map<String, Object>>() {
											@Override
											public int compare(Map<String, Object> o1, Map<String, Object> o2) {
												Date date1 = (Date) o1.get("startDate");
												Date date2 = (Date) o2.get("startDate");
												return date1.compareTo(date2); 
											}
										});
									    Collections.reverse(ans);	
									}
									/*搜索已完成任务*/
									else if(state==1){
										// 搜索已完成任务
//										List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//												.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();
										List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "branchsettleProcess");
										HashSet<Long> idSet = new HashSet<>(); // 存放已经添加的申请业务表的id
										for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
											String processInstanceId = historicTaskInstance.getProcessInstanceId();
											for (PayablesBranchsettle tmp : settleList) {
												HyGroup hyGroup=tmp.getHyGroup();
												if (processInstanceId.equals(tmp.getProcessInstanceId())) {
													HashMap<String, Object> m = new HashMap<>();
													m.put("id", tmp.getId());
													m.put("state", 1);
													m.put("productId", hyGroup.getLine().getPn());
													m.put("startDate", hyGroup.getStartDay());
													m.put("productName",  hyGroup.getLine().getName());
													m.put("supplierName",  hyGroup.getLine().getHySupplier().getSupplierName());
													m.put("money", tmp.getShifuMoney());
													m.put("applyDate", tmp.getApplyTime());
													m.put("applyName", tmp.getApplyName().getName());
													ans.add(m);
												}
											}
										}
										Collections.sort(ans, new Comparator<Map<String, Object>>() {
											@Override
											public int compare(Map<String, Object> o1, Map<String, Object> o2) {
												Date date1 = (Date) o1.get("startDate");
												Date date2 = (Date) o2.get("startDate");
												return date1.compareTo(date2); 
											}
										});
									    Collections.reverse(ans);	
									}
									int page = pageable.getPage();
									int rows = pageable.getRows();
									answer.put("total", ans.size());
									answer.put("pageNumber", page);
									answer.put("pageSize", rows);
									answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows)); // 手动分页？
									json.setSuccess(true);
									if (ans.size() == 0) {
										json.setMsg("未获取到符合条件的数据");
									} 
									else
										json.setMsg("获取成功");
									json.setObj(answer);
									return json;
								}
							}
						}
					}			
			    }
			}
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view")
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			PayablesBranchsettle payablesBranchsettle=payablesBranchsettleService.find(id);
			HyGroup hyGroup=payablesBranchsettle.getHyGroup();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();	
			Map<String,Object> map=new HashMap<String,Object>();
			String processInstanceId = payablesBranchsettle.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> auditList = new LinkedList<>();
			/*审核信息*/
			for (Comment comment : commentList) {
				Map<String, Object> auditMap = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				auditMap.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				auditMap.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					auditMap.put("comment", " ");
					auditMap.put("result", 1);
				} else {
					auditMap.put("comment", str.substring(0, index));
					auditMap.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				auditMap.put("time", comment.getTime());
				auditList.add(auditMap);
			}
			map.put("auditList", auditList);
			/*供应商信息*/
			map.put("payNumber", payablesBranchsettle.getPayNumber());
			map.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
			map.put("accountName", hyGroup.getLine().getContract().getBankList().getAccountName()); //账户名称
			map.put("bankAccount", hyGroup.getLine().getContract().getBankList().getBankAccount());
			map.put("bankName", hyGroup.getLine().getContract().getBankList().getBankName()); //开户行
			map.put("bankCode", hyGroup.getLine().getContract().getBankList().getBankCode()); //银行联行号
			map.put("bankType", hyGroup.getLine().getContract().getBankList().getBankType()); //对公为false,对私为true
			map.put("contractCode", hyGroup.getLine().getContract().getContractCode()); //合同号
			map.put("liable", hyGroup.getLine().getContract().getLiable().getName()); //负责人姓名
			/*团结算信息*/
			List<Map<String, Object>> grouplist = new ArrayList<Map<String, Object>>();	
			Map<String,Object> groupmap=new HashMap<String,Object>();
			groupmap.put("settleType", "团结算");
			groupmap.put("productId", hyGroup.getLine().getPn());
			groupmap.put("lineName", hyGroup.getLine().getName());
			groupmap.put("startDate", hyGroup.getStartDay());
			groupmap.put("creatorName", hyGroup.getCreator().getName()); //建团计调
			BigDecimal allIncome=new BigDecimal(0);
			List<Filter> accountFilter=new ArrayList<>();
			accountFilter.add(Filter.eq("groupId", hyGroup));

			List<RegulategroupAccount> accountList=regulategroupAccountService.findList(null,accountFilter,null);
			if(accountList.size()>0){
				allIncome=allIncome.add(accountList.get(0).getAllIncome()); //总收入
			}
			groupmap.put("allIncome", allIncome);
			BigDecimal allExpense=new BigDecimal(0);
			if(accountList.size()>0){
				/*总收入-扣点-总支出+预付款*/
				allExpense=allExpense.add(accountList.get(0).getAllExpense());
			}
			groupmap.put("allExpense", allExpense);
			groupmap.put("signupNumber", hyGroup.getSignupNumber());
			BigDecimal koudian=new BigDecimal(0);
			if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
				BigDecimal div=new BigDecimal(100);
				/*扣点为总收入乘以扣点的百分数*/
				koudian=koudian.add(allIncome.multiply(hyGroup.getPercentageKoudian()).divide(div).setScale(2, RoundingMode.HALF_UP));
			}
			/*如果是人头扣点,扣点等于每个人扣点乘以报名人数*/
			else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
				koudian=koudian.add(hyGroup.getPersonKoudian().multiply(new BigDecimal(hyGroup.getSignupNumber())));
			}
			groupmap.put("koudian", koudian);
			List<Filter> balanceFilter=new ArrayList<>();
			balanceFilter.add(Filter.eq("groupId", hyGroup.getId()));
			balanceFilter.add(Filter.eq("payStatus", 1)); //付款状态为已付款								
			List<BalanceDueApplyItem> balanceList=balanceDueApplyItemService.findList(null,balanceFilter,null);
			BigDecimal prepay=new BigDecimal(0);							
			if(balanceList.size()>0){
				/*将所有的usePrePay相加，得到预付款prepay*/
				for(BalanceDueApplyItem balanceDueApplyItem:balanceList){
					prepay=prepay.add(balanceDueApplyItem.getUsePrePay());
				}
			}	
			groupmap.put("prepay", prepay);
			groupmap.put("totalMoney",payablesBranchsettle.getRealMoney());
			grouplist.add(groupmap);
			map.put("groupList", grouplist);
			/*退款信息*/
			BigDecimal refundMoney=new BigDecimal(0);//退款
			List<Filter> orderFilter=new ArrayList<>();
			orderFilter.add(Filter.eq("groupId",hyGroup.getId()));
			List<HyOrder> orderList=hyOrderService.findList(null,orderFilter,null);
			List<HyOrderApplication> appList=new ArrayList<HyOrderApplication>();
			/*退款信息*/
			if(orderList.size()>0){
				for(HyOrder hyOrder:orderList){
					List<Filter> appFilter=new ArrayList<Filter>();
					appFilter.add(Filter.in("orderId", hyOrder.getId()));
					appFilter.add(Filter.eq("status", 4)); //筛选已退款状态
					List<HyOrderApplication> applicationList=hyOrderApplicationService.findList(null,appFilter,null);
					if(applicationList.size()>0){
						appList.addAll(applicationList);	
					}									
				}																	
				if(appList.size()>0){
					/*将所有的退款相加的最终的退款*/		
					for(HyOrderApplication hyOrderApplication:appList){
						HashMap<String,Object> obj=new HashMap<String,Object>();
						refundMoney=refundMoney.add(hyOrderApplication.getJiesuanMoney());
						obj.put("settleType", "退款");
						obj.put("orderNumber", hyOrderService.find(hyOrderApplication.getOrderId()).getOrderNumber()); //点单编号
				        obj.put("productId", hyGroup.getLine().getPn());
				        obj.put("lineName", hyGroup.getLine().getName());
				        obj.put("startDate", hyGroup.getStartDay());
				        obj.put("refundDate", hyOrderApplication.getCreatetime()); //退款日期
				        /*游客联系人*/
				        obj.put("contact", hyOrderService.find(hyOrderApplication.getOrderId()).getContact());
					    obj.put("refundReq", hyOrderApplication.getView());
					    obj.put("refundMoney", hyOrderApplication.getJiesuanMoney());
					    list.add(obj);
					}
				}
			}
			map.put("refundList", list);
			map.put("subTotal", refundMoney);
			BigDecimal money=new BigDecimal(0);
			money=money.add(allIncome).subtract(koudian).subtract(allExpense).add(prepay).subtract(refundMoney);
			map.put("yingfu", money);
			json.setMsg("查询列表成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="audit")
	@ResponseBody
	public Json audit(Long id,String comment, Integer state, HttpSession session)
	{
		Json json=new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			PayablesBranchsettle settle=payablesBranchsettleService.find(id);
			String processInstanceId = settle.getProcessInstanceId();
			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			}
			else{
				HashMap<String, Object> map = new HashMap<>();
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();

				if (state == 1){//审核通过
					map.put("result", "tongguo");
					if(settle.getStep()==1){//提交,待市场部副总审核
						settle.setStep(2); //待财务部审核					
					}
					else if(settle.getStep()==2){
						settle.setAuditStatus(2); //设置审核状态为已通过
						settle.setStep(3); //财务部审核通过	
						/*插入分公司产品结算待付款记录*/
						PaySettlement paySettlement=new PaySettlement();
						paySettlement.setHasPaid(0); //未付
						paySettlement.setBranchName(settle.getHyGroup().getGroupCompany().getName()); //分公司名称
						paySettlement.setSettleConfirmCode(settle.getPayNumber());
						paySettlement.setAmount(settle.getShifuMoney()); //实付款
						paySettlement.setAppliName(hyAdmin.getName()); //财务审核人/付款申请人
						paySettlement.setApplyDate(new Date());
						List<Filter> filters=new ArrayList<Filter>();
						filters.add(Filter.eq("hyDepartment",settle.getHyGroup().getGroupCompany()));
						List<HyCompany> hyCompanyList=hyCompanyService.findList(null,filters,null);
						filters.clear();
						filters.add(Filter.eq("hyCompany",hyCompanyList.get(0)));
						List<BankList> bankList=bankListService.findList(null,filters,null);
						paySettlement.setBankListId(bankList.get(0).getId());
						paySettlementService.save(paySettlement);
					} 
				}
				else if(state==0){//驳回
					map.put("result", "bohui");
					settle.setAuditStatus(3); //设置审核状态为已通过
					settle.setStep(0); //设置步骤为驳回,待计调处理
					
					
					//add by wj 2019-07-17  团结算被驳回提醒
					HyAdmin admin = settle.getApplyName();
					Department department = EmployeeUtil.getCompany(admin);
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("hyDepartment",department));//找到父部门
					filters.add(Filter.like("name", "财务部"));
					List<Department> departments = departmentService.findList(null,filters,null);
					if(departments!=null&&departments.size()!=0){
						department = departments.get(0);
						filters.clear();
						filters.add(Filter.eq("department", department));
						List<HyAdmin> admins = hyAdminService.findList(null,filters,null);
						for(HyAdmin admin2:admins){
							String phone = admin2.getMobile();
							SendMessageEMY.sendMessage(phone,"",16);
						}
					}
//					//add by wj 2019-07-07  团结算被驳回提醒
//					HyGroup hyGroup = settle.getHyGroup();
//					String phone = null;
//					if(hyGroup.getCreator()!=null){
//						phone = hyGroup.getCreator().getMobile();
//					}
//					SendMessageEMY.sendMessage(phone,"",16);
				}
			
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId, (comment == null ? " " : comment) + ":" + state);
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId(), map);
				payablesBranchsettleService.update(settle);
				json.setMsg("审核成功");
			    json.setSuccess(true);
			}
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
