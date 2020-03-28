package com.hongyu.controller.gxz04;


import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.*;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.service.*;
import com.hongyu.util.Constants.AuditStatus;
import com.sun.webkit.graphics.Ref;

import javafx.scene.shape.Line;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/admin/storeLineOrder/provider_cancel_group/")
public class XiaotuanShenhe {
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "hyGroupCancelAuditServiceImpl")
	HyGroupCancelAuditService hyGroupCancelAuditService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name = "hyOrderItemServiceImpl")
	HyOrderItemService hyOrderItemService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "groupXiaotuanServiceImpl")
	GroupXiaotuanService groupXiaotuanService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "groupDivideServiceImpl")
	GroupDivideService groupDivideService;
	
	@Resource(name = "groupMemberServiceImpl")
	GroupMemberService groupMemberService;
	
	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	
	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;
	
	@Resource(name = "refundRecordsServiceImpl")
	RefundRecordsService refundRecordsService;
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;
	
	@Resource(name = "groupStoreCancelServiceImpl")
	GroupStoreCancelService groupStoreCancelService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "fddContractServiceImpl")
	FddContractService fddContractService;
	
	@Resource(name = "fddDayTripContractServiceImpl")
	FddDayTripContractService fddDayTripContractService;

	@Autowired
	RefundPayServicerModifyService refundPayServicerModifyService;

	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	SupplierDismissOrderApplyService supplierDismissOrderApplyService;
	
	
	/**
	 * 审核列表页-待加入筛选条件部分
	 * @param pageable 分页信息
	 * @param shenheStatus
	 * @param session
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, String shenheStatus, HyGroupCancelAudit query, HttpSession session, String supplierName) {
	Json j = new Json();	
		
		try {	
			//得到登录用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(query);
			List<HyGroupCancelAudit> hyGroupCancelAudits = hyGroupCancelAuditService.findList(null, filters, null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (shenheStatus == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyGroupCancelAudit tmp : hyGroupCancelAudits) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							 
							if(task.getTaskDefinitionKey().equals("usertask2")) {
								//修改逻辑 从groupStoreCancel中找寻记录，若有就说明已经审核，不返回给前端了
								Store store = storeService.findStore(hyAdminService.find(username));
								
								if(store == null) {
									throw new RuntimeException("当前登录员工不属于任何门店");
								}
								Long storeId = store.getId();
								Long groupId = tmp.getHyGroup().getId();
								List<Filter> filters1 = new ArrayList<>();
								filters1.add(Filter.eq("groupId", groupId));
								filters1.add(Filter.eq("storeId", storeId));
								List<GroupStoreCancel> gscs = groupStoreCancelService.findList(null, filters1, null);
								if(gscs.isEmpty()) {
									helpler(tmp, ans, "daishenhe", supplierName);//待审核数据	
								} //说明该门店还没有审核	
							} else {
								helpler(tmp, ans, "daishenhe", supplierName);//待审核数据	
							}
							
							
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyGroupCancelAudit tmp : hyGroupCancelAudits) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							
							List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
							
							String str = "";
							for(Comment c : comment){
								if(username.equals(c.getUserId()))
										{
									str = c.getFullMessage();
									break;
										}
									
							}
							
							String[] strs = str.split(":");
							if(strs.length >= 2) {
								if(strs[1] == null) {
									throw new RuntimeException("状态错误");
								}
								helpler(tmp, ans, strs[1], supplierName);
							}
							
						}
					}
				}
			} else if (shenheStatus.equals("daishenhe")) {// 搜索未完成任务
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyGroupCancelAudit tmp : hyGroupCancelAudits) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							
							if(task.getTaskDefinitionKey().equals("usertask2")) {
								//修改逻辑 从groupStoreCancel中找寻记录，若有就说明已经审核，不返回给前端了
								Store store = storeService.findStore(hyAdminService.find(username));
								
								if(store == null) {
									throw new RuntimeException("当前登录员工不属于任何门店");
								}
								Long storeId = store.getId();
								Long groupId = tmp.getHyGroup().getId();
								List<Filter> filters1 = new ArrayList<>();
								filters1.add(Filter.eq("groupId", groupId));
								filters1.add(Filter.eq("storeId", storeId));
								List<GroupStoreCancel> gscs = groupStoreCancelService.findList(null, filters1, null);
								if(gscs.isEmpty()) {
									helpler(tmp, ans, "daishenhe", supplierName);//待审核数据	
								} //说明该门店还没有审核		
							} else {
								helpler(tmp, ans, "daishenhe", supplierName);//待审核数据
							}
											
								
						}
					}
				}

			} else if (shenheStatus.equals("yishenhe")) {// 搜索已审核任务
				//add 20190416修改已审核任务筛选
//				Store store = storeService.findStore(hyAdminService.find(username));
//				
//				if(store == null) {
//					throw new RuntimeException("当前登录员工不属于任何门店");
//				}
//				List<Filter> fs = new ArrayList<>();
//				fs.add(Filter.eq("storeId", store.getId()));
//				List<GroupStoreCancel> ls = groupStoreCancelService.findList(null, filters, null);
//				
//				for(GroupStoreCancel temp : ls) {
//					fs.clear();
//					fs.add(Filter.eq("hyGroup", hyGroupService.find(temp.getGroupId())));
//					List<HyGroupCancelAudit> gcas = hyGroupCancelAuditService.findList(null, fs, null);
//					if(!gcas.isEmpty() && gcas.get(0) != null) {
//						helpler(gcas.get(0), ans, "yitongguo", supplierName);
//					}
//				}
				
				
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyGroupCancelAudit tmp : hyGroupCancelAudits) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							
						    List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
							
							String str = "";
							for(Comment c : comment){
								if(username.equals(c.getUserId()))
										{
									str = c.getFullMessage();
									break;
										}
									
							}
							
							String[] strs = str.split(":");
							if(strs.length >= 2) {
								if(strs[1] == null) {
									throw new RuntimeException("状态错误");
								}
								helpler(tmp, ans, strs[1], supplierName);
							}
							
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
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(answer);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	public boolean isStore(HyAdmin admin){
		if(admin.getDepartment()!=null){
			Department department = admin.getDepartment();
			if(department.getHyDepartmentModel()!=null){
				String name = department.getHyDepartmentModel().getName();
				if(name!=null && name.contains("门店"))					
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 消团申请的ID
	 * @param id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id, HttpSession session) {
		Json j = new Json();
		
		try {
				List<Filter> filters = new ArrayList<>();
				
				HyGroupCancelAudit hyGroupCancelAudit = hyGroupCancelAuditService.find(id);						

				HyGroup group = hyGroupCancelAudit.getHyGroup();
				
				Map<String, Object> map = new HashMap<String, Object>(); //最后结果
								
				/** 审核详情 */
				List<HashMap<String, Object>> shenheMap = new ArrayList<>();
				
				filters.add(Filter.eq("cancleGroupId", group.getId()));
				filters.add(Filter.eq("type", 2));
				
				//判断登录角色，门店只可以看到自己的记录
				String username1 = (String) session.getAttribute(CommonAttributes.Principal);
				HyAdmin curOperator = hyAdminService.find(username1);
				if(isStore(curOperator)){
					List<Filter> orderFilters = new ArrayList<>();
					orderFilters.add(Filter.eq("operator", curOperator));
					List<HyOrder> orders = hyOrderService.findList(null, orderFilters, null);
					List<Long> orderIds = new ArrayList<>();
					for(HyOrder o:orders){
						orderIds.add(o.getId());
					}
					filters.add(Filter.in("orderId", orderIds));
				}
				
				List<HyOrderApplication> aps = hyOrderApplicationService.findList(null, filters, null);
				
				List<HyOrderApplicationItem> items = new ArrayList<>();
				
				for(HyOrderApplication temp : aps) {
					if(temp.getCancleGroupId() != null) {
						HyGroup hyGroup = hyGroupService.find(temp.getCancleGroupId());
						temp.setStartDate(hyGroup.getStartDay());
					}
					if(temp.getOrderId() != null) {
						HyOrder hyOrder = hyOrderService.find(temp.getOrderId());
						temp.setOrderNumber(hyOrder.getOrderNumber());
					}			
					items.addAll(temp.getHyOrderApplicationItems());
				}
				
				map.put("hyOrderApplications", aps);
				map.put("hyOrderApplicationItems", items);
								
				/**
				 * 审核详情添加
				 */
				String processInstanceId = hyGroupCancelAudit.getProcessInstanceId();
				List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
				Collections.reverse(commentList);
				for (Comment comment : commentList) {
					HashMap<String, Object> im = new HashMap<>();
					String taskId = comment.getTaskId();
					HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
							.singleResult();
					String step = "";
					if (task != null) {
						step = task.getName();
					}
					im.put("step", step);
					String username = comment.getUserId();
					HyAdmin hyAdmin = hyAdminService.find(username);
					String name = "";
					if (hyAdmin != null) {
						name = hyAdmin.getName();
					}
					im.put("auditName", name);
					String str = comment.getFullMessage();
					String[] strs = str.split(":");
					
				    im.put("comment", strs[0]);
				    if(strs[1].equals("yitongguo")) {
				    	im.put("result", "通过");
				    } else if (strs[1].equals("yibohui")) {
				    	im.put("result", "驳回");
				    } else {
				    	im.put("result", "提交审核");
				    }
					
					im.put("time", comment.getTime());

					shenheMap.add(im);
				}
				
				map.put("auditRecord", shenheMap);
				j.setMsg("查看详情成功");
				j.setSuccess(true);
				j.setObj(map);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 审核(通过或者驳回)消团申请
	 * @param id 消团申请id comment 驳回批注   shenheStatus 审核状态
	 * 		
	 * @return
	 */
	@Transactional
	@RequestMapping(value="audit")
	public Json audit(Long id, String comment, String shenheStatus, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyGroupCancelAudit hyGroupCancelAudit = hyGroupCancelAuditService.find(id);
			
			String processInstanceId = hyGroupCancelAudit.getProcessInstanceId();
			
			HyGroup group = hyGroupCancelAudit.getHyGroup();
			
			Long groupId = group.getId();


			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				HashMap<String, Object> map = new HashMap<>(); 
				
				
				if(shenheStatus.equals("yitongguo")) {					
					
						map.put("result", "tongguo");
						if(task.getTaskDefinitionKey().equals("usertask2")) { //如果是门店审核需要单独处理，因为有三个流转的地方
							
							//设置下一阶段审核的部门  --- 根据审核额度不同设置不同
							List<Filter> filters = new ArrayList<>();
							filters.add(Filter.eq("eduleixing", Eduleixing.xiaotuanLimit));
							List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
							
							BigDecimal money = edu.get(0).getMoney();
							
							//获取现在是不是最后一个门店审核
							filters.clear();
							filters.add(Filter.eq("groupId", groupId));
							List<GroupXiaotuan> groupXiaotuans = groupXiaotuanService.findList(null, filters, null);
							
							int number = 1;
							GroupXiaotuan gx = null;
							if(!groupXiaotuans.isEmpty()) {
								gx = groupXiaotuans.get(0);							
								number = gx.getNumber();
							}
							
							//门店每审核通过一次就将数量减去一
							number --;
							gx.setNumber(number);							
							groupXiaotuanService.update(gx); //将数量减去1并更新到数据库
							
							if(number == 0) { //如果门店全部审核通过就往下一步走
								if(hyGroupCancelAudit.getMoney().compareTo(money) > 0) {
									map.put("money", "more");
								} else {
									map.put("money", "less");
								}
								Authentication.setAuthenticatedUserId(username);
								taskService.claim(task.getId(),username);
								taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
								taskService.complete(task.getId(), map);	
								//start of add 审核通过改变退款orderApplication的状态
								List<Filter> orderApplicationFilters = new ArrayList<>();
								orderApplicationFilters.add(Filter.eq("cancleGroupId", groupId));
								
								List<HyOrderApplication> orderApplicationList = hyOrderApplicationService.findList(null, orderApplicationFilters, null);
								for(HyOrderApplication temp : orderApplicationList) {
									temp.setStatus(1);
									hyOrderApplicationService.update(temp);
								}								
								//end of add
							}	
							
							//add by gxz20181015 每次审核都将团和门店ID写入数据库，主要用于列表页筛选
							Store store = storeService.findStore(hyAdminService.find(username));
							if(null != store) {
								GroupStoreCancel groupStoreCancel = new GroupStoreCancel();
								groupStoreCancel.setStoreId(store.getId());
								groupStoreCancel.setGroupId(groupId);
								groupStoreCancelService.save(groupStoreCancel);
								
							}
							
						} else if (task.getTaskDefinitionKey().equals("usertask3")) {
							Authentication.setAuthenticatedUserId(username);
							taskService.claim(task.getId(),username);
							taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
							taskService.complete(task.getId(), map);
							//start of add 审核通过改变退款orderApplication的状态
							List<Filter> orderApplicationFilters = new ArrayList<>();
							orderApplicationFilters.add(Filter.eq("cancleGroupId", groupId));
							
							List<HyOrderApplication> orderApplicationList = hyOrderApplicationService.findList(null, orderApplicationFilters, null);
							for(HyOrderApplication temp : orderApplicationList) {
								temp.setStatus(2);
								hyOrderApplicationService.update(temp);
							}								
							//end of add
						} else if (task.getTaskDefinitionKey().equals("usertask4")) { //总公司财务审核通过
							hyGroupCancelAudit.setAuditStatus(AuditStatus.pass);
							//设置团的状态							
							group.setGroupState(GroupStateEnum.yiquxiao);
							hyGroupService.update(group);
							Authentication.setAuthenticatedUserId(username);
							taskService.claim(task.getId(),username);
							taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
							taskService.complete(task.getId(), map);
							hyGroupCancelAuditService.update(hyGroupCancelAudit);
							
							/**
							 * 更新线路的最低价格,added by GSbing,20181016 
							 */
							HyLine hyLine=group.getLine();
							BigDecimal lineLowestPrice=hyLine.getLowestPrice();
							//如果正在消的团正好是最低价格,重新设置线路最低价格
							if(group.getLowestPrice().compareTo(lineLowestPrice)<=0) {
								BigDecimal lowestPrice=hyLineService.getLineLowestPrice(hyLine);
								hyLine.setLowestPrice(lowestPrice);
								hyLineService.update(hyLine);
							}
							
							
							//groupdivide中group_id相同的数据删除
							
							/*
							 * write by lbc
							 * change by cwz 2018-9-10
							 */
							
							List<Filter> filters1 = new ArrayList<>();
							filters1.add(Filter.eq("group", group));
							List<GroupDivide> groupDivides = groupDivideService.findList(null, filters1, null);
							for(GroupDivide groupDivide : groupDivides) {
								groupDivideService.delete(groupDivide);
							}
							//增加删除另一个表
							List<Filter> filters2 = new ArrayList<>();
							filters2.add(Filter.eq("hyGroup", group));
							List<GroupMember> groupMembers = groupMemberService.findList(null, filters2, null);
							for(GroupMember groupMember : groupMembers) {
								groupMemberService.delete(groupMember);
							}
							// 消团以后改变订单状态
							//修改，只有在审核通过以后才改变订单状态
							List<Filter> filters = new ArrayList<>();
							filters.add(Filter.eq("status", 0)); //待支付的订单变为已取消
							filters.add(Filter.eq("groupId", groupId));
							List<HyOrder> orders = hyOrderService.findList(null, filters, null);

							for(HyOrder temp : orders) {
								//消团发送短信

								temp.setStatus(6);
								hyOrderService.update(temp);
							}

							filters.clear();
							filters.add(Filter.eq("status", 2)); //待供应商确认的订单变为已取消
							filters.add(Filter.eq("groupId", groupId));
							List<HyOrder> orders1 = hyOrderService.findList(null, filters, null);
							for(HyOrder temp : orders1) {
								/*temp.setStatus(5);
								hyOrderService.update(temp);
								supplierDismissOrderApplyService.addSupplierDismissOrderSubmit(temp.getId(), "供应商消团", session);*/
								temp.setStatus(6);
								hyOrderService.update(temp);
							}
							
							/**
							 * 销团之后，需要将该团的线路合同取消
							 * liyang
							 * 20190118
							 */
							List<Filter> filters11 = new ArrayList<>();
							filters11.add(Filter.eq("groupId", group.getId()));
							filters11.add(Filter.eq("type", 1));
							filters11.add(Filter.isNotNull("contractId"));
							filters11.add(Filter.isNotNull("contractNumber"));
							List<HyOrder> hyOrders = hyOrderService.findList(null,filters11,null);
							if(hyOrders!=null){
								for(HyOrder hyOrder:hyOrders){
									Long contractId = hyOrder.getContractId();
									if(contractId!=null && hyOrder.getContractNumber()!=null){
										if(hyOrder.getContractType()==0){
											FddDayTripContract curr = fddDayTripContractService.find(contractId);
											curr.setStatus(5);
											curr.setCancelDate(new Date());
										}else{
											FddContract curr = fddContractService.find(contractId);
											curr.setStatus(5);
											curr.setCancelDate(new Date());
										}
									}
								}
							}		
							
							/**
							 * write by wj  
							 * 门店预存款记录 
							 */
							//根据HyOrderApplication表拿到该团所有报名的门店
							List<Filter> filters3 = new ArrayList<>();
							filters3.add(Filter.eq("cancleGroupId", group.getId()));
							filters3.add(Filter.eq("type", 2));  
//							filters3.add(Filter.eq("status", 0));//之前的审核过程没有更改type值，所以到总公司财务审核之后状态还是0
							List<HyOrderApplication> orderApplications = hyOrderApplicationService.findList(null,filters3,null);
							for(HyOrderApplication orderApplication:orderApplications){
								HyOrder hyOrder = hyOrderService.find(orderApplication.getOrderId());
								hyOrder.setRefundstatus(2);
								hyOrderService.update(hyOrder);
								
								Long storeId = hyOrder.getStoreId();
								Long orderId = hyOrder.getId();
								Store store = storeService.find(storeId);
							
								HySupplierContract contract = new HySupplierContract();
								List<Filter> filters22 = new ArrayList<>();
								HyAdmin supplier = hyOrder.getSupplier();
								if(supplier.getHyAdmin()!=null){
									supplier = supplier.getHyAdmin();
								}
					            filters.add(Filter.eq("liable", supplier));
					            List<HySupplierContract> contracts = hySupplierContractService.findList(null,filters22,null);
					            for(HySupplierContract con : contracts){
					            	if(con.getContractStatus().equals(ContractStatus.zhengchang)){
					            		contract = con;
					            	}
					            }
					            if(contract.getId() == null) 
					            	throw new Exception("没有正常状态的合同");
					            
								refundPayServicerModifyService.xiaotuan(hyOrder, orderApplication, contract, username, store);
//								BigDecimal tuiKuan = new BigDecimal(0);
//								//通过hy_order_application_item表拿到该门店的退款金额
//							    List<HyOrderApplicationItem> hyOrderApplicationItems = orderApplication.getHyOrderApplicationItems();
//							    for(HyOrderApplicationItem hyOrderApplicationItem:hyOrderApplicationItems){
//							    	tuiKuan = tuiKuan.add(hyOrderApplicationItem.getJiesuanRefund()).add(hyOrderApplicationItem.getBaoxianWaimaiRefund());
//							    }
//								
//								//写入退款记录   //预存款余额修改
////								BigDecimal tuiKuan = hyOrder.getJiesuanTuikuan();
//								RefundInfo refundInfo = new RefundInfo();
//								refundInfo.setAmount(tuiKuan);
//								refundInfo.setAppliName(orderApplication.getOperator().getName());
//								refundInfo.setApplyDate(orderApplication.getCreatetime());
//								Date date = new Date();
//								refundInfo.setPayDate(date);
//								refundInfo.setRemark("供应商消团退款");
//								refundInfo.setState(1);  //已付款
//								refundInfo.setType(2);  //供应商消团
//								refundInfo.setOrderId(orderId);
//								refundInfoService.save(refundInfo);
//								
//								
//								//生成退款记录
//								RefundRecords records = new RefundRecords();
//								records.setRefundInfoId(refundInfo.getId());
//								records.setOrderCode(hyOrder.getOrderNumber());
//								records.setOrderId(hyOrder.getId());
//								records.setRefundMethod((long) 1); //预存款方式
//								records.setPayDate(date);
//								HyAdmin hyAdmin = hyAdminService.find(username);
//								if(hyAdmin!=null)
//									records.setPayer(hyAdmin.getName());
//								records.setAmount(tuiKuan);
//								records.setStoreId(storeId);
//								records.setStoreName(store.getStoreName());
//								records.setTouristName(hyOrder.getContact());
//								records.setTouristAccount(store.getBankList().getBankAccount());  //门店账号
//								records.setSignUpMethod(1);   //门店
//								refundRecordsService.save(records);
//
//								PayandrefundRecord record = new PayandrefundRecord();
//								record.setOrderId(orderId);
//								record.setMoney(tuiKuan);
//								record.setPayMethod(5);	//5预存款
//								record.setType(1);	//1退款
//								record.setStatus(1);	//1已退款
//								record.setCreatetime(date);
//								payandrefundRecordService.save(record);
//										
//								//预存款余额表
//								// 3、修改门店预存款表      并发情况下的数据一致性！
//								
//								filters.clear();
//								filters.add(Filter.eq("store", store));
//								List<StoreAccount> list = storeAccountService.findList(null, filters, null);
//								if(list.size()!=0){
//									StoreAccount storeAccount = list.get(0);
//									storeAccount.setBalance(storeAccount.getBalance().add(tuiKuan));
//									storeAccountService.update(storeAccount);
//								}else{
//									StoreAccount storeAccount = new StoreAccount();
//									storeAccount.setStore(store);
//									storeAccount.setBalance(tuiKuan);
//									storeAccountService.save(storeAccount);
//								}
//								
//								// 4、修改门店预存款记录表
//								StoreAccountLog storeAccountLog = new StoreAccountLog();
//								storeAccountLog.setStatus(1);
//								storeAccountLog.setCreateDate(orderApplication.getCreatetime());
//								storeAccountLog.setMoney(tuiKuan);
//								storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
//								storeAccountLog.setStore(store);
//								storeAccountLog.setType(4);	//供应商消团
//								storeAccountLog.setProfile("供应商消团");
//								storeAccountLogService.update(storeAccountLog);
//								
//								// 5、修改 总公司-财务中心-门店预存款表
//								StorePreSave storePreSave = new StorePreSave();
//								storePreSave.setStoreId(storeId);
//								storePreSave.setStoreName(store.getStoreName());
//								storePreSave.setType(2); ////1:门店充值 2:报名退款  3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款 10:酒店/酒加景销售 11:酒店/酒加景退款 12:门店后返 13:供应商驳回订单 14:
//								storePreSave.setDate(date);
//								storePreSave.setAmount(tuiKuan);
//								storePreSave.setOrderCode(hyOrder.getOrderNumber());
//								storePreSave.setOrderId(orderId);
//								storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters, null).get(0).getBalance());
//								storePreSaveService.save(storePreSave); 
							}
							group.setIsCancel(true);
							
							//start of add 审核通过改变退款orderApplication的状态
							List<Filter> orderApplicationFilters = new ArrayList<>();
							orderApplicationFilters.add(Filter.eq("cancleGroupId", groupId));
							
							List<HyOrderApplication> orderApplicationList = hyOrderApplicationService.findList(null, orderApplicationFilters, null);
							for(HyOrderApplication temp : orderApplicationList) {
								temp.setStatus(4);
								hyOrderApplicationService.update(temp);
							}								
							//end of add
						}
				} else if (shenheStatus.equals("yibohui")) {
					map.put("result", "bohui");
					hyGroupCancelAudit.setAuditStatus(AuditStatus.notpass);
					Authentication.setAuthenticatedUserId(username);
					taskService.claim(task.getId(),username);
					taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
					taskService.complete(task.getId(), map);
					hyGroupCancelAuditService.update(hyGroupCancelAudit);
					
					//驳回成功以后将该团有关的消团记录清除
					List<Filter> fs = new ArrayList<>();
					fs.add(Filter.eq("groupId", groupId));
					List<GroupStoreCancel> gscs = groupStoreCancelService.findList(null, fs, null);
					for(GroupStoreCancel tmp : gscs) {
						groupStoreCancelService.delete(tmp);
					} //驳回以后清空数据库
					//start of add 审核通过改变退款orderApplication的状态
					List<Filter> orderApplicationFilters = new ArrayList<>();
					orderApplicationFilters.add(Filter.eq("cancleGroupId", groupId));
					
					List<HyOrderApplication> orderApplicationList = hyOrderApplicationService.findList(null, orderApplicationFilters, null);
					for(HyOrderApplication temp : orderApplicationList) {
						temp.setStatus(5);
						hyOrderApplicationService.update(temp);
					}								
					//end of add
				}
			}
			
			json.setMsg("审核成功");
			json.setSuccess(true);
		}catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}
	
	private void helpler(HyGroupCancelAudit hyGroupCancelAudit, List<Map<String, Object>> ans, String status, String supplierName) {
		HyGroup group = hyGroupCancelAudit.getHyGroup();
		
		
		if(group != null) {
			HyLine line = group.getLine();
			HySupplier supplier = line.getHySupplier();
			String sName = supplier.getSupplierName();
			if(supplierName != null && !sName.contains(supplierName)) {
				return;
			}
			HashMap<String, Object> m = new HashMap<>();
			
			m.put("id", hyGroupCancelAudit.getId());
			m.put("shenheStatus", status);	
			HyAdmin operator = group.getCreator();
			
			m.put("pn", line.getPn());
			m.put("startDay", group.getStartDay());
			m.put("endDay", group.getEndDay());
			m.put("name", line.getName());
			
			m.put("supplierName", sName);
			if(operator != null) {
				m.put("operator", operator.getName());
			}
			m.put("applyTime", hyGroupCancelAudit.getApplyTime());
			ans.add(m);
		}	
		
		
	}
	
}
