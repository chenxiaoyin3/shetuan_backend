package com.hongyu.controller.cwz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyReceiptRefundService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketSceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.PiaowuConfirmService;
import com.hongyu.service.StoreService;
import com.hongyu.service.SupplierDismissOrderApplyService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.util.liyang.EmployeeUtil;

//这个作为供应商审核什么的接口 第三套流程


//接口负责接收后半部分的审核流程，复制到这里，上个接口不动
@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/thirdticket/thirdaudit/")
public class TicketAuditHaveToChangeThree {

	@Resource(name = "hyTicketSceneServiceImpl")
	private HyTicketSceneService hyTicketSceneService;
	
	@Resource(name = "hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name = "hyTicketSceneTicketManagementServiceImpl")
	private HyTicketSceneTicketManagementService hyTicketSceneTicketManagementService;
	
	@Resource(name = "hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	@Resource(name = "commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "insuranceServiceImpl")
	private InsuranceService insuranceService;
	
	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name = "hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	@Resource(name="supplierDismissOrderApplyServiceImpl")
	private SupplierDismissOrderApplyService supplierDismissOrderApplyService;
	
	@Resource(name="hyReceiptRefundServiceImpl")
	private HyReceiptRefundService hyReceiptRefundService;
	
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private TaskService taskService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name="hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;
	
	@Resource (name = "piaowuConfirmServiceImpl")
	private PiaowuConfirmService piaowuConfirmService;
	 
	 
		
		//******************************下面是退款还有售后的*********************************
	 
		//有一个向application表里存的还没有写，写在下订单的地方
		
		// 添加实收付款记录 这个应该是前端说的那个
		static class ReceiptRefund {
			public Long orderId;
			public BigDecimal money;
			public Integer type;
			public String method;
			public Date collectionTime;
			public String remark;
			public String bankNum;
			public String cusName;
			public String cusBank;
			public String cusUninum;
			public String reason;
			public BigDecimal adjustMoney;
		}
		
		@Resource(name = "hyCompanyServiceImpl")
		HyCompanyService hyCompanyService;		
		
		@Resource(name = "departmentServiceImpl")
		DepartmentService departmentService;
		
		static class MyOrderItem implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private Long itemId;
			private String name;
			private Integer type;
			private Integer priceType;
			private Integer number;
			private BigDecimal jiesuanPrice;
			private BigDecimal jiesuanRefund;
			private BigDecimal waimaiPrice;
			private BigDecimal waimaiRefund;
			private BigDecimal baoxianJiesuanPrice;
			private BigDecimal baoxianJiesuanRefund;
			private BigDecimal baoxianWaimaiPrice;
			private BigDecimal baoxianWaimaiRefund;
			//2018-11-16 修改一下 加一个字段
			private ArrayList<String> customerNames;
			private String customerName;
			private Integer numberOfReturn;//退货数量

			public ArrayList<String> getCustomerNames() {
				return customerNames;
			}

			public void setCustomerNames(ArrayList<String> customerNames) {
				this.customerNames = customerNames;
			}

			public Long getItemId() {
				return itemId;
			}

			public void setItemId(Long itemId) {
				this.itemId = itemId;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public Integer getType() {
				return type;
			}

			public void setType(Integer type) {
				this.type = type;
			}

			public Integer getPriceType() {
				return priceType;
			}

			public void setPriceType(Integer priceType) {
				this.priceType = priceType;
			}

			public Integer getNumber() {
				return number;
			}

			public void setNumber(Integer number) {
				this.number = number;
			}

			public BigDecimal getJiesuanPrice() {
				return jiesuanPrice;
			}

			public void setJiesuanPrice(BigDecimal jiesuanPrice) {
				this.jiesuanPrice = jiesuanPrice;
			}

			public BigDecimal getJiesuanRefund() {
				return jiesuanRefund;
			}

			public void setJiesuanRefund(BigDecimal jiesuanRefund) {
				this.jiesuanRefund = jiesuanRefund;
			}

			public BigDecimal getWaimaiPrice() {
				return waimaiPrice;
			}

			public void setWaimaiPrice(BigDecimal waimaiPrice) {
				this.waimaiPrice = waimaiPrice;
			}

			public BigDecimal getWaimaiRefund() {
				return waimaiRefund;
			}

			public void setWaimaiRefund(BigDecimal waimaiRefund) {
				this.waimaiRefund = waimaiRefund;
			}

			public BigDecimal getBaoxianJiesuanPrice() {
				return baoxianJiesuanPrice;
			}

			public void setBaoxianJiesuanPrice(BigDecimal baoxianJiesuanPrice) {
				this.baoxianJiesuanPrice = baoxianJiesuanPrice;
			}

			public BigDecimal getBaoxianJiesuanRefund() {
				return baoxianJiesuanRefund;
			}

			public void setBaoxianJiesuanRefund(BigDecimal baoxianJiesuanRefund) {
				this.baoxianJiesuanRefund = baoxianJiesuanRefund;
			}

			public BigDecimal getBaoxianWaimaiPrice() {
				return baoxianWaimaiPrice;
			}

			public void setBaoxianWaimaiPrice(BigDecimal baoxianWaimaiPrice) {
				this.baoxianWaimaiPrice = baoxianWaimaiPrice;
			}

			public BigDecimal getBaoxianWaimaiRefund() {
				return baoxianWaimaiRefund;
			}

			public void setBaoxianWaimaiRefund(BigDecimal baoxianWaimaiRefund) {
				this.baoxianWaimaiRefund = baoxianWaimaiRefund;
			}

			public String getCustomerName() {
				return customerName;
			}

			public void setCustomerName(String customerName) {
				this.customerName = customerName;
			}

			public Integer getNumberOfReturn() {
				return numberOfReturn;
			}

			public void setNumberOfReturn(Integer numberOfReturn) {
				this.numberOfReturn = numberOfReturn;
			}

		}
		
		
		//门店售后
		@RequestMapping(value = "store_customer_service/list/view")
		@ResponseBody
		public Json storeCustomerServiceList(Pageable pageable, Integer status, String providerName, HttpSession session) {
			return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session,
					HyOrderApplication.STORE_CUSTOMER_SERVICE,4);	//门票订单类型为4
		}
		
		
		//门店退款审核详情页
		@RequestMapping(value = { "store_refund/detail/view", "store_customer_service/detail/view" })
		@ResponseBody
		public Json storeRefundDetail(Long id) {
			Json json = new Json();

			try {
				HyOrderApplication application = hyOrderApplicationService.find(id);
				if (application == null) {
					throw new Exception("没有有效的审核申请记录");
				}
				@SuppressWarnings("unused")
				HyOrder order = hyOrderService.find(application.getOrderId());

				Map<String, Object> ans = new HashMap<>();

				/** 审核详情需要注意 这个修改完了*/
				ans.put("application", hyOrderApplicationService.auditDetailHelper(application, application.getStatus()));
				/** 审核条目详情需要注意 这个就走其他无保险类 不需要修改*/
				ans.put("applicationItems", hyOrderApplicationService.auditItemsHelper(application));

				/**
				 * 审核详情添加
				 */
				String processInstanceId = application.getProcessInstanceId();
				List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
				Collections.reverse(commentList);

				List<Map<String, Object>> auditList = new ArrayList<>();
				for (Comment comment : commentList) {
					Map<String, Object> map = new HashMap<>();
					String taskId = comment.getTaskId();
					HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
							.singleResult();
					String step = "";
					if (task != null) {
						step = task.getName();
					}
					//2018-11-19 这个加一个控制
					if(step.equals("门店提出退团"))
						step = "门店提出退票";
					
					map.put("step", step);
					String username = comment.getUserId();

					HyAdmin admin = hyAdminService.find(username);
					String name = "";
					if (admin != null) {
						name = admin.getName();
					}
					map.put("auditName", name);
					
					String fullMsg = comment.getFullMessage();
					
					String[] msgs = fullMsg.split(":");
					map.put("comment", msgs[0]);
					if (msgs[1].equals("0")) {
						map.put("result", "驳回");
					} else if (msgs[1].equals("1")) {
						map.put("result", "通过");
					}

					map.put("time", comment.getTime());

					auditList.add(map);
				}

				ans.put("auditRecords", auditList);

				json.setSuccess(true);
				json.setMsg("查看详情成功");
				json.setObj(ans);

			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("查看详情失败");
				json.setObj(e.getMessage());
			}
			return json;
		}	
			
	

		@RequestMapping(value = "store_customer_service/audit", method = RequestMethod.POST)
		@ResponseBody
		public Json storeCustomerServiceAudit(Long id, String comment, Integer auditStatus, HttpSession session) {
			Json json = new Json();
			try {
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				HyOrderApplication application = hyOrderApplicationService.find(id);
				@SuppressWarnings("unused")
				String applyName = application.getOperator().getUsername(); // 找到提交申请的人
				String processInstanceId = application.getProcessInstanceId();

				if (processInstanceId == null || processInstanceId.equals("")) {
					throw new Exception("审核出错，信息不完整，请重新申请");
				}

				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				HashMap<String, Object> map = new HashMap<>(); // 保存流转信息和流程变量信息
																// 下一阶段审核的部门

				if (auditStatus.equals(1)) { // 如果审核通过
					map.put("msg", "true");
					if (task.getTaskDefinitionKey().equals("usertask2")) { // 如果供应商
						// 设置下一阶段审核的部门 ---
						List<Filter> filters = new ArrayList<>();
						/**审核额度需要注意 */
						filters.add(Filter.eq("eduleixing", Eduleixing.storeShouHouLimit));
						List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
						BigDecimal money = edu.get(0).getMoney();
						BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianJiesuanMoney());
						if (tuiKuan.compareTo(money) > 0) { // 如果退款总额大于限额，
							map.put("money", "more"); // 设置需要品控中心限额审核
							application.setStatus(1); // 待品控限额审核
						} else { // 如果退款总额不大于限额
							map.put("money", "less"); // 设置财务审核
							application.setStatus(2); // 待财务审核
						}
					} else if (task.getTaskDefinitionKey().equals("usertask3")) { // 如果品控
						application.setStatus(2); // 待财务审核
					} else if (task.getTaskDefinitionKey().equals("usertask4")) {
		
						/**财务审核通过需要注意*/
						// 售前退款财务审核通过，进行订单处理
						hyOrderApplicationService.handleTicketHotelandsceneScs(application);
						
						application.setStatus(4);//已退款
						
						//售前退款财务审核通过，请王劼同学添加相关操作
						piaowuConfirmService.shouhouPiaowuRefund(application, username, 3, "门店门票售后退款");
						
					}

				} else {
					map.put("msg", "false");
					application.setStatus(5); // 已驳回
					HyOrder order = hyOrderService.find(application.getOrderId());
					order.setRefundstatus(4); // 退款已驳回
					hyOrderService.update(order);
					
					// add by wj 2019/7/20  门票售后驳回短信提示
					HyAdmin admin = order.getOperator();
					if(admin != null){
						String phone = admin.getMobile();
						SendMessageEMY.sendMessage(phone, "", 21);
					}

				}
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId,
						(comment == null ? "审核通过" : comment) + ":" + auditStatus);
				taskService.complete(task.getId(), map);
				hyOrderApplicationService.update(application);
				json.setSuccess(true);
				json.setMsg("审核成功");
			} catch (Exception e) {
				
				json.setSuccess(false);
				json.setMsg("审核失败");
				e.printStackTrace();
			}
			return json;
		}	
	

}
