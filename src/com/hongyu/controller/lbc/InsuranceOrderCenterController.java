package com.hongyu.controller.lbc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.PayServicer;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.ReceiptServicer;
import com.hongyu.entity.ReceiptTotalServicer;
import com.hongyu.entity.Store;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationItemService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyReceiptRefundService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.PayablesLineItemService;
import com.hongyu.service.PayablesLineService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.PaymentSupplierService;
import com.hongyu.service.ReceiptServicerService;
import com.hongyu.service.ReceiptTotalServicerService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.RefundRecordsService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreService;
import com.hongyu.service.SupplierDismissOrderApplyService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.liyang.EmployeeUtil;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;






@Controller
@RequestMapping("/admin/insuranceordercenter/")
public class InsuranceOrderCenterController {
	@Resource(name = "payablesLineItemServiceImpl")
	PayablesLineItemService payablesLineItemService;

	@Resource(name = "payablesLineServiceImpl")
	PayablesLineService payablesLineService;

	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;

	@Resource(name = "hyReceiptRefundServiceImpl")
	HyReceiptRefundService hyReceiptRefundService;

	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;

	@Resource(name = "hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;

	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;

	@Resource
	private RuntimeService runtimeService;

	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;

	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;

	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;

	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	SupplierDismissOrderApplyService supplierDismissOrderApplyService;

	@Resource(name = "groupMemberServiceImpl")
	GroupMemberService groupMemberService;

	@Resource(name = "groupDivideServiceImpl")
	GroupDivideService groupDivideService;
	
	@Resource(name = "paymentSupplierServiceImpl")
	PaymentSupplierService paymentSupplierService;

	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;
	
	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;
	
	@Resource(name = "refundRecordsServiceImpl")
	RefundRecordsService refundRecordsService;
	
	@Resource(name = "receiptTotalServicerServiceImpl")
	ReceiptTotalServicerService receiptTotalServicerService;
	
	@Resource(name = "receiptServicerServiceImpl")
	ReceiptServicerService receiptServicerService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;
	
	@Resource(name = "hyOrderApplicationItemServiceImpl")
	private HyOrderApplicationItemService hyOrderApplicationItemService;
	
	@Resource(name = "hyOrderCustomerServiceImpl")
	private HyOrderCustomerService hyOrderCustomerService;
	
	@Resource(name = "insuranceServiceImpl")
	private InsuranceService insuranceService;
	
	@Resource(name = "hyDepartmentModelServiceImpl")
	private HyDepartmentModelService hyDepartmentModelService;
	
	public static class Wrap{
		HyOrder hyOrder;
		Date startTime;
		Date endTime;
		public HyOrder getHyOrder() {
			return hyOrder;
		}
		public void setHyOrder(HyOrder hyOrder) {
			this.hyOrder = hyOrder;
		}
		public Date getStartTime() {
			return startTime;
		}
		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}
		public Date getEndTime() {
			return endTime;
		}
		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}
		
	}
	
	/**
	 * 订单中心列表
	 * @author LBC
	 */
	@RequestMapping("list/view")
	@ResponseBody
	public Json InsuranceOrderCenterlist(Pageable pageable, @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime, HttpSession session, 
			HttpServletRequest request, Integer paystatus, Integer type, 
			Integer refundstatus, String orderNumber, String insuranceName, @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endDate, String customerName) {
		Json json = new Json();
		try {
//			HashMap<String, Object> hm = new HashMap<String, Object>();
//			List<HashMap<String, Object>> result = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			HyOrder hyOrder = new HyOrder();
			hyOrder.setPaystatus(paystatus);
			hyOrder.setRefundstatus(refundstatus);
			hyOrder.setOrderNumber(orderNumber);
			hyOrder.setName(insuranceName);
			//保险订单
			hyOrder.setType(6);
			//类型 国内/境外
			hyOrder.setXianlutype(type);

			/**
			 * 获取用户权限范围
			 */
			
			//暂时保留权限功能
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			
			List<Filter> filters = new LinkedList<>();
			//根据用户名筛选显示的订单
			filters.add(Filter.in("operator", hyAdmins));
			
			//如果前端需要筛选保险起期
			if(startTime != null) {
				filters.add(Filter.ge("fatuandate", startTime));
			}
			if(endTime != null) {
				filters.add(Filter.le("fatuandate", endTime));
			}
			
			if(startDate != null) {
				filters.add(Filter.ge("createtime", startDate));
			}
			if(endDate != null) {
				filters.add(Filter.le("createtime", endDate));
			}
			if(customerName != null) {
				List<Filter> customerFilter = new LinkedList<>();
				customerFilter.add(Filter.like("name", customerName));
				List<HyOrderCustomer> hyOrderCustomers = hyOrderCustomerService.findList(null, customerFilter, null);
				List<HyOrderItem> hyOrderItems = new LinkedList<>();
				for(HyOrderCustomer hyOrderCustomer : hyOrderCustomers) {
					hyOrderItems.add(hyOrderCustomer.getOrderItem());
				}
				Set<Long> hyOrderIds = new HashSet<>();
				for(HyOrderItem hyOrderItem : hyOrderItems) {
					hyOrderIds.add(hyOrderItem.getOrder().getId());
				}
				filters.add(Filter.in("id", hyOrderIds));
			}
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createtime"));
			pageable.setOrders(orders);
			pageable.setFilters(filters);
			Page<HyOrder> page = hyOrderService.findPage(pageable, hyOrder);
			List<Map<String, Object>> result = new LinkedList<>();
			for (HyOrder tmp : page.getRows()) {
				Map<String, Object> map = new HashMap<>();
				HyAdmin operator = tmp.getOperator();
				map.put("id", tmp.getId());
				map.put("status", tmp.getStatus());
				map.put("orderNumber", tmp.getOrderNumber());
				map.put("name", tmp.getName());
//				int source1 = tmp.getSource();
//				if(source1 != Constants.mendian){
//					map.put("storeName", "");
//				}else{
//					Long storeId = tmp.getStoreId();
//					Store store = storeService.find(storeId);
//					map.put("storeName", store==null?"":store.getStoreName());
//				}
				map.put("people", tmp.getPeople());
				map.put("jiusuanMoney", tmp.getJiusuanMoney());
				map.put("waimaiMoney", tmp.getWaimaiMoney());
				
				//合同类型   0--国内一日游  1--国内游  2--境外游
//				Integer contractType = 0;
//				if(tmp.getXianlutype()!=null){
//					if(tmp.getXianlutype()>3){
//						//大于3即为出境类型
//						contractType = 2;
//					}else{
//						if(tmp.getTianshu()>1){
//							contractType = 1;
//						}else{
//							contractType = 0;
//						}
//					}
//				}
//				map.put("contractType",contractType);
//				map.put("contractNumber", tmp.getContractNumber());
				
				map.put("createtime", tmp.getCreatetime());
				if (operator.equals(admin)) {
					if (co == CheckedOperation.view) {
						map.put("privilege", "view");
					} else {
						map.put("privilege", "edit");
					}
				} else {
					if (co == CheckedOperation.edit) {
						map.put("privilege", "edit");
					} else {
						map.put("privilege", "view");
					}
				}
				
				Long storeId = tmp.getStoreId();
				if(storeId!=null){
					Store store = storeService.find(storeId);
					map.put("storeName", store==null?"":store.getStoreName());
				}else{
					map.put("storeName", "");
				}
				Long groupId = tmp.getGroupId();
				if(groupId!=null){
					HyGroup hyGroup = hyGroupService.find(groupId);
					map.put("linePn", hyGroup.getGroupLinePn());
				}
				
				map.put("fatuandate", tmp.getFatuandate());
				map.put("tianshu", tmp.getTianshu());
				map.put("type", tmp.getXianlutype());
				
				result.add(map);
			}
			Map<String, Object> hMap = new HashMap<>();
			hMap.put("pageNumber", page.getPageNumber());
			hMap.put("pageSize", page.getPageSize());
			hMap.put("total", page.getTotal());
			hMap.put("rows", result);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询错误： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}


	/**
	 * 订单详情
	 * @author LBC
	 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			
			HyOrder tmp = hyOrderService.find(id);
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", tmp.getId()));
			//退款状态是部分退款
			if(tmp.getRefundstatus() == 3) {
				//过滤掉已撤保的
				filters.add(Filter.ne("status", 4));
			}
			
			InsuranceOrder insuranceOrder = insuranceOrderService.findList(null, filters, null).get(0);
			Map<String, Object> map = new HashMap<>();	
			map.put("id", tmp.getId());
			map.put("orderNumber", tmp.getOrderNumber());
			map.put("name", tmp.getName());
			map.put("status", tmp.getStatus());
			map.put("paystatus", tmp.getPaystatus());
			map.put("refundstatus", tmp.getRefundstatus());
//			map.put("type", tmp.getType());
			map.put("people", tmp.getPeople());
			map.put("tianshu", tmp.getTianshu());
			map.put("contact", tmp.getContact());
//			Long storeId = tmp.getStoreId();
//			if(storeId!=null){
//				Store store = storeService.find(storeId);
//				map.put("storeName", store==null?"":store.getStoreName());
//			}else{
//				map.put("storeName", "");
//			}
			map.put("storeType", tmp.getStoreType());
			map.put("operator", tmp.getOperator());
			map.put("creatorId", tmp.getCreatorId());
			map.put("waimaiMoney", tmp.getWaimaiMoney());
			map.put("baoxianJiesuanTuikuan", tmp.getBaoxianJiesuanTuikuan());
			map.put("baoxianWaimaiTuikuan", tmp.getBaoxianWaimaiTuikuan());
			map.put("ifjiesuan", tmp.getIfjiesuan());
			map.put("insuranceOrderDownloadUrl", tmp.getInsuranceOrderDownloadUrl());
			map.put("departure", tmp.getDeparture());
			map.put("fatuandate", tmp.getFatuandate());
			
			
			if(tmp.getStatus() == 3) {
				map.put("shengxiaodate", insuranceOrder.getInsuranceStarttime());
			}
			else {
				map.put("shengxiaodate", null);
			}
			
			
			
			map.put("tianshu", tmp.getTianshu());
			map.put("xianlumingcheng", tmp.getXianlumingcheng());
			//国内 境外
			map.put("type", tmp.getXianlutype());
			map.put("contact", tmp.getContact());
			map.put("phone", tmp.getPhone());
			map.put("remark", tmp.getRemark());
			
			map.put("createtime", tmp.getCreatetime());
			map.put("modifytime", tmp.getModifytime());
			
			map.put("orderItems", tmp.getOrderItems());
			map.put("insurancestatus", insuranceOrder.getStatus());

//			//查找额外保险信息
//			Insurance insurance = insuranceService.getExtraInsuranceOfOrder(tmp);
//			if(insurance!=null){
//				map.put("insurance",insurance.getInsuranceCode());
//				map.put("insuranceMoney", insurance.getInsurancePrices());
//			}
//			Long groupId = tmp.getGroupId();
//			if(groupId!=null){
//				HyGroup hyGroup = hyGroupService.find(groupId);
//				map.put("linePn", hyGroup.getGroupLinePn());
//			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	/**
	 * 产品详情
	 * @author LBC
	 */
	@RequestMapping("productList/view")
	@ResponseBody
	public Json productList(Pageable pageable, Long id) {
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			//通过hyOrder找到相应的InsuranceOrder
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", hyOrder.getId()));
			//退款状态是部分退款
			if(hyOrder.getRefundstatus() == 3) {
				//过滤掉已撤保的
				filters.add(Filter.ne("status", 4));
			}
			InsuranceOrder insuranceOrder = insuranceOrderService.findList(null, filters, null).get(0);
			List<Map<String, Object>> result = new LinkedList<>();
			for (HyOrderItem tmp : hyOrder.getOrderItems()) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", tmp.getId());
				//map.put("customer", tmp.getHyOrderCustomers().get(0).getName());
				map.put("customer", tmp.getHyOrderCustomers().get(0));
				map.put("jtPolicyNo", insuranceOrder.getJtPolicyNo());
				map.put("jtInsureNo", insuranceOrder.getJtInsureNo());
				map.put("insurancestatus", insuranceOrder.getStatus());
				map.put("name", tmp.getName());
				map.put("type", tmp.getType());
				map.put("orderstatus", hyOrder.getStatus());
				map.put("price", tmp.getWaimaiPrice());
				result.add(map);
			}
			int pageNumber = pageable.getPage();
			int pageSize = pageable.getRows();
			Map<String, Object> hMap = new HashMap<>();
			hMap.put("pageNumber", pageNumber);
			hMap.put("pageSize", pageSize);
			hMap.put("total", result.size());
			hMap.put("rows", result.subList((pageNumber - 1) * pageSize,
					pageNumber * pageSize > result.size() ? result.size() : pageNumber * pageSize));
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hMap);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 顾客列表
	 * @author LBC
	 */
	@RequestMapping("customerList/view")
	@ResponseBody
	public Json customerList(Pageable pageable, Long id) {
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			List<HyOrderCustomer> result = new LinkedList<>();
			for (HyOrderItem tmp : hyOrder.getOrderItems()) {
				if (tmp.getHyOrderCustomers() != null && tmp.getHyOrderCustomers().size() > 0) {
					result.addAll(tmp.getHyOrderCustomers());
				}
			}
			int pg = pageable.getPage();
			int rows = pageable.getRows();
			Map<String, Object> map = new HashMap<>();
			map.put("total", result.size());
			map.put("pageNumber", pg);
			map.put("pageSize", rows);
			map.put("rows", result.subList((pg - 1) * rows, pg * rows > result.size() ? result.size() : pg * rows));
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	/**
	 * 订单日志
	 * @param pageable
	 * @param id
	 * @return
	 */
	@RequestMapping("applicationList/view")
	@ResponseBody
	public Json applicationList(Pageable pageable, Long id) {
		Json json = new Json();
		try {
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("orderId", id));
			pageable.setFilters(filters);
			List<Order> orders = new LinkedList<>();

			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<HyOrderApplication> page = hyOrderApplicationService.findPage(pageable);
			List<Map<String, Object>> result = new LinkedList<>();
			
			for(HyOrderApplication hyOrderApplication : page.getRows()) {
				Map<String, Object> map = new HashMap<>();
				map.put("baoxianJiesuanMoney", hyOrderApplication.getBaoxianJiesuanMoney());
				map.put("baoxianWaimaiMoney", hyOrderApplication.getBaoxianWaimaiMoney());
				map.put("cancleGroupId", hyOrderApplication.getCancleGroupId());
				map.put("content", hyOrderApplication.getContent());
				map.put("createtime", hyOrderApplication.getCreatetime());
				map.put("id", hyOrderApplication.getId());
				map.put("isSubstatis", hyOrderApplication.getIsSubStatis());
				map.put("jiesuanMoney", hyOrderApplication.getJiesuanMoney());
				map.put("operator", hyOrderApplication.getOperator().getName());
				map.put("orderId", hyOrderApplication.getOrderId());
				map.put("orderNumber", hyOrderApplication.getOrderNumber());
				map.put("outcome", hyOrderApplication.getOutcome());
				map.put("processInstanceId", hyOrderApplication.getProcessInstanceId());
				map.put("status", hyOrderApplication.getStatus());
				map.put("type", hyOrderApplication.getType());
				map.put("view", hyOrderApplication.getView());
				map.put("hyOrderApplicationItems", hyOrderApplication.getHyOrderApplicationItems());
				
				
				
				result.add(map);
			}
			Map<String, Object> hMap = new HashMap<>();
			hMap.put("pageNumber", page.getPageNumber());
			hMap.put("pageSize", page.getPageSize());
			hMap.put("total", page.getTotal());
			hMap.put("rows", result);
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询错误： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	/**
	 * 实收付款记录列表
	 * @author LBC
	 */
	// 实收付款记录列表
	@RequestMapping("receipt_refund/list")
	@ResponseBody
	public Json receiptRefundList(Long id, Integer type) {
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}

			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("order", order));
			filters.add(Filter.eq("type", type));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createTime"));
			List<HyReceiptRefund> receiptRefunds = hyReceiptRefundService.findList(null, filters, orders);

			json.setSuccess(true);
			json.setMsg("查找成功");
			json.setObj(receiptRefunds);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	// 添加实收付款记录
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

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	/**
	 * 添加实收付款记录
	 * @author LBC
	 */
	@RequestMapping(value = "receipt_refund/add", method = RequestMethod.POST)
	@ResponseBody
	public Json addReceiptRefund(@RequestBody ReceiptRefund body, HttpSession session) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);

		Json json = new Json();
		try {
			Long orderId = body.orderId; // 订单id
			if (orderId == null) {
				throw new Exception("没有订单参数");
			}
			HyOrder order = hyOrderService.find(orderId);
			if (order == null) {
				throw new Exception("没有有效订单");
			}
			BigDecimal money = body.money; // 收退款钱数

			if (money == null) {
				throw new Exception("传入的钱数有误");
			}

			Integer type = body.type; // 类型

			String method = body.method; // 收退款方式

			Date collectionTime = body.collectionTime; // 收退款时间

			String remark = body.remark; // 备注
			
			String bankNum = body.bankNum;	//银行卡号
			
			String cusName = body.cusName;	//游客姓名
			
			String cusBank = body.cusBank;	//游客银行
			
			String cusUninum = body.cusUninum;	//游客联行号
			
			String reason = body.reason;	//原因
			
			BigDecimal adjustMoney = body.adjustMoney;	//调整金额

			HyReceiptRefund receiptRefund = new HyReceiptRefund();

			receiptRefund.setCollectionTime(collectionTime);
			receiptRefund.setCreateTime(new Date());
			receiptRefund.setMethod(method);
			
			receiptRefund.setOperator(admin);
			receiptRefund.setOrder(order);
			receiptRefund.setRemark(remark);
			receiptRefund.setStore(storeService.findStore(admin));
			receiptRefund.setType(type);
			receiptRefund.setBankNum(bankNum);
			receiptRefund.setStatus(0);	//待分公司财务确认
			receiptRefund.setBranch(departmentService.findCompanyOfDepartment(admin.getDepartment()));
			receiptRefund.setCusName(cusName);
			receiptRefund.setCusBank(cusBank);
			receiptRefund.setCusUninum(cusUninum);
			receiptRefund.setReason(reason);
			receiptRefund.setAdjustMoney(adjustMoney==null?BigDecimal.ZERO:adjustMoney);
			receiptRefund.setMoney(money);
			hyReceiptRefundService.save(receiptRefund);
			json.setSuccess(true);
			json.setMsg("添加成功"); 
			json.setObj(receiptRefund.getId());

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("添加失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 取消订单
	 * @author LBC
	 */
	// 取消线路订单
	@RequestMapping(value = "order_cancel")
	@ResponseBody
	public Json cancel(Long id, HttpSession session) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);

		Json json = new Json();
		try {
			
			hyOrderService.cancelInsuranceOrder(id);
			HyOrderApplication application = new HyOrderApplication();
			application.setContent("门店取消订单");
			application.setOperator(admin);
			application.setOrderId(id);
			application.setCreatetime(new Date());
			application.setStatus(HyOrderApplication.STATUS_ACCEPT);
			//门店取消订单
			application.setType(HyOrderApplication.STORE_CANCEL_ORDER);
			hyOrderApplicationService.save(application);

			json.setSuccess(true);
			json.setMsg("取消成功");
			json.setObj(null);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("取消失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	

    static class MyOrderItem implements Serializable {
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
		private String customerName;

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

	}

	

//	@RequestMapping(value = "order_refund/audit_list/view")
//	@ResponseBody
//	public Json OrderRefundAuditList(Pageable pageable,Integer status, @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
//			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime, HttpSession session) {
//		return this.getAuditList(pageable, status, session, startTime, endTime);
//	}
//
//	public Json getAuditList(Pageable pageable, Integer status, HttpSession session, @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
//			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime) {
//		/**
//		 * 获取当前用户
//		 */
//		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		Json json = new Json();
//		try {
//
//			List<Filter> applyFilters = new ArrayList<>();
//			if(status != null) {
//				//通过的 已退款
//				if(status == 0) {
//					applyFilters.add(Filter.eq("status", 1));
//				}
//				//驳回的 已驳回
//				else if(status == 1) {
//					applyFilters.add(Filter.eq("status", 2));
//				}
//				//未审核（待财务审核）
//				else if(status == 2){
//					applyFilters.add(Filter.eq("status", 0));
//				}
//				
//			}
//			//门店撤保退款
//			applyFilters.add(Filter.ge("type", HyOrderApplication.STORE_INSURANCE_REFUND));
//			applyFilters.add(Filter.le("type", HyOrderApplication.OFFICIAL_WEBSITE_REFUND));
//			
//			List<Order> orders = new ArrayList<>();
//			orders.add(Order.desc("createtime"));
//			
//			if(startTime != null) {
//				applyFilters.add(Filter.ge("createtime", startTime));
//			}
//			if(endTime != null) {
//				applyFilters.add(Filter.le("createtime", endTime));
//			}
//			
//			List<HyOrderApplication> ans = hyOrderApplicationService.findList(null, applyFilters, orders);
//			List<Map<String, Object> > list = new ArrayList<>();
//			for(int i = 0; i < ans.size(); i++) {
//				Map<String, Object> map = new HashMap<>();
//				map.put("hyOrderApplication", ans.get(i));
//				HyOrder hyOrder = hyOrderService.find(ans.get(i).getOrderId());
//				map.put("insuranceName", hyOrder.getName());
//				map.put("orderNumber", hyOrder.getOrderNumber());
//				map.put("orderCreateTime", hyOrder.getCreatetime());
//				map.put("store", storeService.find(hyOrder.getStoreId()));
//				map.put("remark", ans.get(i).getView());
//				list.add(map);
//			}
//
//			int page = pageable.getPage();
//			int rows = pageable.getRows();
//			Page<Map<String, Object>> pages = new Page<>(new ArrayList<Map<String, Object>>(), 0, pageable);
//			if (list != null && !list.isEmpty()) {
//				pages.setTotal(list.size());
//				pages.setRows(list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows));
//			}
//
//			json.setSuccess(true);
//			json.setMsg("获取成功");
//			json.setObj(pages);
//
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setSuccess(false);
//			json.setMsg("获取失败");
//			json.setObj(e.getMessage());
//			e.printStackTrace();
//		}
//		return json;
//	}
//
//	//因为只有一个审核步骤，所以没有审核详情
//	@RequestMapping(value = "order_refund/audit_detail/view" )
//	@ResponseBody
//	public Json OrderRefundAuditDetail(Long id) {
//		Json json = new Json();
//
//		try {
//			HyOrderApplication application = hyOrderApplicationService.find(id);
//			if (application == null) {
//				throw new Exception("没有有效的审核申请记录");
//			}
//			HyOrder order = hyOrderService.find(application.getOrderId());
//
//			Map<String, Object> ans = new HashMap<>();
//			Map<String, Object> map = new HashMap<>();
//			map.put("id", application.getId());
//			map.put("status", application.getStatus());
//			if(order == null){
//				return null;
//			}
//			map.put("orderNumber", order.getOrderNumber());	//订单编号
//			map.put("source", order.getSource());	//订单来源
//			if(order.getStoreId()!=null){
//				Store store = storeService.find(order.getStoreId());
//				map.put("storeName", store.getStoreName());
//			}
//			
//			
//			map.put("contactName", order.getOperator().getName());	//门店联系人姓名
//			map.put("contactPhone", order.getOperator().getMobile());	//门店联系人电话
//			map.put("jiesuanMoney", order.getJiesuanMoney1());	//订单结算金额
//			map.put("jiesuanRefund", application.getJiesuanMoney());	//结算退款金额
//			map.put("waimaiMoney",order.getWaimaiMoney());	//订单外卖金额
//			map.put("waimaiRefund", application.getWaimaiMoney());	//订单外卖退款
//			map.put("baoxianJiesuanRefund", application.getBaoxianJiesuanMoney());	//保险结算退款金额
//			map.put("baoxianWaimaiRefund", application.getBaoxianWaimaiMoney());	//保险外卖退款金额
//
//			map.put("startDay", order.getFatuandate());	//保险日期
//			map.put("tianshu", order.getTianshu());	//保险天数
//			map.put("lineName", order.getXianlumingcheng());	//线路名称
//			
//			
//			map.put("applyTime", application.getCreatetime());	//申请日期
//
//			/** 审核详情 */
//			ans.put("application", map);
//			List<Map<String, Object>> list = new ArrayList<>();
//			for(HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
//				HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
//				Map<String, Object> map1 = new HashMap<>();
//				if(orderItem == null) {
//					map1.put("id", item.getId());
//					map1.put("itemId", null);
//					map1.put("jiesuanRefund", item.getJiesuanRefund());	
//					map1.put("waimaiRefund", item.getWaimaiRefund());
//					map1.put("baoxianJiesuanRefund", item.getBaoxianJiesuanRefund());
//					map1.put("baoxianWaimaiRefund", item.getBaoxianWaimaiRefund());
//				}
//				else {
//					map1.put("id", item.getId());
//					map1.put("itemId", orderItem.getId());
//					map1.put("type", orderItem.getType());
//					map1.put("priceType", orderItem.getPriceType());
//					map1.put("name", orderItem.getName());
//					map1.put("number", orderItem.getNumber());
//					map1.put("jiesuanPrice", orderItem.getJiesuanPrice());
//					map1.put("jiesuanRefund", item.getJiesuanRefund());
//					map1.put("waimaiPrice", orderItem.getWaimaiPrice());
//					map1.put("waimaiRefund", item.getWaimaiRefund());
//					map1.put("baoxianJiesuanPrice", orderItem.getJiesuanPrice());
//					map1.put("baoxianJiesuanRefund", item.getBaoxianJiesuanRefund());
//					map1.put("baoxianWaimaiPrice", orderItem.getJiesuanPrice());
//					map1.put("baoxianWaimaiRefund", item.getBaoxianWaimaiRefund());
//					map1.put("status", orderItem.getStatus());
//				}
//				list.add(map1);
//			}
//			
//			
//			
//			
//			ans.put("applicationItems", list);
//
//
//			json.setSuccess(true);
//			json.setMsg("查看详情成功");
//			json.setObj(ans);
//
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setSuccess(false);
//			json.setMsg("查看详情失败");
//			json.setObj(e.getMessage());
//		}
//		return json;
//	}

    /**
	 * 订单退款申请
	 * @author LBC
	 */
	@Transactional
	@RequestMapping(value = "order_refund/apply", method = RequestMethod.POST)
	@ResponseBody
	public Json storeInsuranceRefundApply(@RequestBody HyOrderApplication application, HttpSession session) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(application.getOrderId());
			if (order == null) {
				throw new Exception("订单无效");
			}
			if(order.getFatuandate().compareTo(new Date())<0) {
				json.setSuccess(false);
				json.setMsg("保险生效后不能退款");
				json.setObj(2);
				return json;
			}
			//HyGroup group = hyGroupService.find(order.getGroupId());

			Map<String, Object> variables = new HashMap<>();

			//保险不涉及审核流程
			//HyAdmin provider = group.getCreator();

			// 指定审核供应商
			//variables.put("provider", provider.getUsername());

			// 启动流程
//			ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeTuiTuan", variables);
//			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
//
//			Authentication.setAuthenticatedUserId(username);
//			taskService.addComment(task.getId(), pi.getProcessInstanceId(), "发起申请:1");
//			taskService.complete(task.getId(), variables);

			application.setContent("门店保险退款");
			application.setOperator(admin);
			application.setStatus(0); // 待财务审核
			application.setCreatetime(new Date());
//			application.setProcessInstanceId(task.getProcessInstanceId());
			application.setType(HyOrderApplication.STORE_INSURANCE_REFUND);//门店保险退款 13 14 15
			
			
			order.setRefundstatus(1); // 订单退款状态为退款中
			//order.setStatus(2);//订单状态为退款中
			//
			hyOrderService.update(order);
			BigDecimal baoxianJiesuanRefund = new BigDecimal(0);
			BigDecimal baoxianWaimaiRefund = new BigDecimal(0);
			

			for (HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
				//在后续退款处理的时候会set值
//				HyOrderItem hyOrderItem = hyOrderItemService.find(item.getItemId());
//				hyOrderItem.setNumberOfReturn(item.getReturnQuantity());
//				hyOrderItemService.update(hyOrderItem);
				baoxianWaimaiRefund = baoxianWaimaiRefund.add(item.getBaoxianWaimaiRefund());
				baoxianJiesuanRefund = baoxianJiesuanRefund.add(item.getBaoxianJiesuanRefund());
//				System.out.println(item.getBaoxianJiesuanRefund());
//				System.out.println(item.getBaoxianWaimaiRefund());
				item.setHyOrderApplication(application);
			}
			
//			System.out.println(baoxianJiesuanRefund);
//			System.out.println(baoxianWaimaiRefund);
			application.setBaoxianJiesuanMoney(baoxianJiesuanRefund);
			application.setBaoxianWaimaiMoney(baoxianWaimaiRefund);

			hyOrderApplicationService.save(application);
			
			 
			List<String> userIds = new ArrayList<>();
			HyDepartmentModel model = hyDepartmentModelService.find("总公司财务部");
			Set<Department> dsDepartments = model.getHyDepartments();
			Set<HyAdmin> admins = new HashSet<>();
			for(Department d : dsDepartments) {
				admins.addAll(d.getHyAdmins());
			}
			
			for(HyAdmin admin2 : admins) {
				HyRole role = admin2.getRole();
				if(role.getHyRoleAuthorities().size() > 0) {
					for(HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
						if(Constants.CaiwuAuditStoreTuiTuan.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
							//SendMessageEMY.sendMessage(admin.getMobile(), "", 9);
							userIds.add(admin2.getUsername());
						}
					}
				}
			}
			
			String content = "您有保险退款工作需要审核，请尽快完成";
			SendMessageQyWx.sendWxMessage(QyWxConstants.ZONG_BU_CAI_WU_QYWX_APP_AGENT_ID, userIds, null, content);
			
			
			
			json.setSuccess(true);
			json.setMsg("保险退款申请成功");
			json.setObj(null);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("保险退款申请失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

//	@Transactional
//	@RequestMapping(value = "order_refund/audit", method = RequestMethod.POST)
//	@ResponseBody
//	public Json storeInsuranceOrderRefundAudit(Long id, String remark, Integer type, HttpSession session) {
//		Json json = new Json();
//		try {
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			
//			//用 orderapplication
//			HyOrderApplication application = hyOrderApplicationService.find(id);
//			String applyName = application.getOperator().getUsername(); // 找到提交申请的人
////			String processInstanceId = application.getProcessInstanceId();
////
////			if (processInstanceId == null || processInstanceId.equals("")) {
////				throw new Exception("审核出错，信息不完整，请重新申请");
////			}
//
////			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
//			HashMap<String, Object> map = new HashMap<>(); // 保存流转信息和流程变量信息
//															// 下一阶段审核的部门
//
//			if (type.equals(0)) { // 如果审核通过
//				map.put("msg", "true");
//				
//				boolean isPartRefund = false;
//				//查看是部分退款 还是全部退款
//				//对每一个applicationOrderItem
//				
//				BigDecimal allRefund = new BigDecimal("0");
//				
//				HyOrder hyOrder = hyOrderService.find(application.getOrderId());
//				
//				//有效的orderitems的size
//				int disabledSize = 0;
//				for(HyOrderItem hyOrderItem : hyOrder.getOrderItems()) {
//					if(hyOrderItem.getStatus().equals(1)) {
//						//无效
//						disabledSize ++;
//					}
//				}
//				
//				//有效的orderitem比退的多，那么只退了一部分
//				if(hyOrder.getOrderItems().size() - disabledSize > application.getHyOrderApplicationItems().size()) {
//					isPartRefund = true;
//				}
//				
//				//退款的时候应该先退全款 再
////				for(HyOrderApplicationItem hyOrderApplicationItem : application.getHyOrderApplicationItems()) {
////					allRefund = allRefund.add(hyOrderApplicationItem.getBaoxianWaimaiRefund());
////				}
//				
//				//应该退全款
//				allRefund = hyOrder.getWaimaiMoney();
//			
//				
//				
//				//不是部分退款 全部退款
//				//if(isPartRefund == false) {
//				/* add by liyang,change the insurance order status */
//				//部分退款的话也变成撤保状态
//				/**
//				 * 因为只有撤保之后状态才能变成已撤保
//				 * 在没有调用撤保接口之前先改变状态会出现数据混乱
//				 */
////				List<Filter> insurancefilters = new ArrayList<>();
////				insurancefilters.add(Filter.eq("orderId", application.getOrderId()));
////				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null, insurancefilters, null);
////				if(!insuranceOrders.isEmpty()){
////					for(InsuranceOrder tmp:insuranceOrders){
////						//调用撤保接口
////						//Json res = insuranceOrderService.cancelInsuranceOrder(tmp.getId());					
////						tmp.setStatus(4);
////						insuranceOrderService.update(tmp);
////					}
////				}
//				//}
//				
//				
//				
//				// 门店退团退款审核成功，进行订单处理
//				hyOrderApplicationService.handleBaoXianTuiKuan(application, isPartRefund);
//
//				
//				// write by wj
//				application.setStatus(1); // 已退款
//				
//				Long orderId = application.getOrderId();
//				Long storeId = hyOrder.getStoreId();
//				Store store = storeService.find(storeId);
//				
//				//只有保险退款
//				//写入退款记录   //预存款余额修改
//				//BigDecimal tuiKuan = application.getBaoxianJiesuanMoney();
//				BigDecimal tuiKuan = allRefund;
//				RefundInfo refundInfo = new RefundInfo();
//				refundInfo.setAmount(tuiKuan);
//				refundInfo.setAppliName(application.getOperator().getName());
//				refundInfo.setApplyDate(application.getCreatetime());
//				Date date = new Date();
//				refundInfo.setPayDate(date);
//				refundInfo.setRemark("门店保险退款");
//				refundInfo.setState(1);  //已付款
//				refundInfo.setType(14);  //保险退款（游客退团，认为是一个类型）
//				refundInfo.setOrderId(orderId);
//				refundInfoService.save(refundInfo);
//				
//				
//				//生成退款记录
//				RefundRecords records = new RefundRecords();
//				records.setRefundInfoId(refundInfo.getId());
//				records.setOrderCode(hyOrder.getOrderNumber());
//				records.setOrderId(hyOrder.getId());
//				records.setRefundMethod((long) 1); //预存款方式
//				records.setPayDate(date);
//				HyAdmin hyAdmin = hyAdminService.find(username);
//				if(hyAdmin!=null)
//					records.setPayer(hyAdmin.getName());
//				records.setAmount(tuiKuan);
//				records.setStoreId(storeId);
//				records.setStoreName(store.getStoreName());
//				records.setTouristName(hyOrder.getContact());
//				records.setTouristAccount(store.getBankList().getBankAccount());  //门店账号
//				records.setSignUpMethod(1);   //门店
//				refundRecordsService.save(records);
//
//				PayandrefundRecord record = new PayandrefundRecord();
//				record.setOrderId(orderId);
//				record.setMoney(tuiKuan);
//				record.setPayMethod(5);	//5预存款
//				record.setType(1);	//1退款
//				record.setStatus(1);	//1已退款
//				record.setCreatetime(date);
//				payandrefundRecordService.save(record);
//						
//				//预存款余额表
//				// 3、修改门店预存款表      并发情况下的数据一致性！
//				
//				List<Filter> filters = new ArrayList<>();
//				filters.add(Filter.eq("store", store));
//				List<StoreAccount> list = storeAccountService.findList(null, filters, null);
//				if(list.size()!=0){
//					StoreAccount storeAccount = list.get(0);
//					storeAccount.setBalance(storeAccount.getBalance().add(tuiKuan));
//					storeAccountService.update(storeAccount);
//				}else{
//					StoreAccount storeAccount = new StoreAccount();
//					storeAccount.setStore(store);
//					storeAccount.setBalance(tuiKuan);
//					storeAccountService.save(storeAccount);
//				}
//				
//				// 4、修改门店预存款记录表
//				StoreAccountLog storeAccountLog = new StoreAccountLog();
//				storeAccountLog.setStatus(1);
//				storeAccountLog.setCreateDate(application.getCreatetime());
//				storeAccountLog.setMoney(tuiKuan);
//				storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
//				storeAccountLog.setStore(store);
//				storeAccountLog.setType(13);
//				storeAccountLog.setProfile("保险退款");
//				storeAccountLogService.update(storeAccountLog);
//				
//				// 5、修改 总公司-财务中心-门店预存款表
//				StorePreSave storePreSave = new StorePreSave();
//				storePreSave.setStoreName(store.getStoreName());
//				storePreSave.setStoreId(store.getId());
//				storePreSave.setType(19); ////1:门店充值 2:报名退款  3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款 10:酒店/酒加景销售 11:酒店/酒加景退款 12:门店后返 13:供应商驳回订单 14:
//				//19保险退款
//				storePreSave.setDate(date);
//				storePreSave.setAmount(tuiKuan);
//				storePreSave.setOrderCode(hyOrder.getOrderNumber());
//				storePreSave.setOrderId(orderId);
//				storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters, null).get(0).getBalance());
//				storePreSaveService.save(storePreSave);
//				
//				Long[] orderIds = new Long[1];
//				orderIds[0] = hyOrder.getId();
//				
//				//部分退款则先退款再 重新下单
//				if(isPartRefund) {
//					json = insuranceOrderService.cancelOrder(orderIds);
//					Map<String, Object> map2 = (Map<String, Object>)json.getObj();
//					//List<HyOrderItem> orderItems = hyOrder.getOrderItems();
//					List<Object> cancelSuccessIds = (List<Object>)map2.get("cancelSuccessIds"); 
//					//退保成功
//					if(cancelSuccessIds.size() >= 1) {
//						//删除退款人的orderItem 之后重新下单
//						
//						//change in 2018/11/19 不在删除，而是将status置位1标识
////						for(HyOrderApplicationItem hyOrderApplicationItem : application.getHyOrderApplicationItems()) {
////							//删除
////							hyOrderItemService.delete(hyOrderApplicationItem.getItemId());
////							for(int i = 0; i < orderItems.size(); i++) {
////								if(orderItems.get(i).getId().equals(hyOrderApplicationItem.getItemId())) {
////									orderItems.remove(i);
////									i--;
////								}
////							}
////						}
////						hyOrder.setOrderItems(orderItems);
//						
//						
//						//退款状态改为部分退款 不能再撤保
//						//hyOrder.setRefundstatus(3);
//						
//						//重新下单到保险订单表
//						insuranceOrderService.generateStoreInsuranceOrder(hyOrder, session);
//						
//						hyOrder.setRefundstatus(3);
//						
//						//下单到江泰
//						json = insuranceOrderService.postOrderToJT(orderIds);
//						Map<String,Object> map1 = (Map<String,Object>)json.getObj();
//						List<Map<String,Object>> list1 = (List<Map<String,Object>>) (map.get("successIds"));
//						//下单到江泰失败
//						if(list.size() < 1) {
//							json.setSuccess(false);
//							json.setMsg("重新下单到江泰失败： ");
//							return json;
//						}
//						//支付
//						json = hyOrderService.addInsuranceOrderPayment(hyOrder.getId(), session);
//						
//						
//						
//						
//						
//						hyOrderService.update(hyOrder);
//						
//						
//					}
//					else {
//						return json;
//					}
//				}
//				else {
//					//全部退款
//					json = insuranceOrderService.cancelOrder(orderIds);
//					if(json.getObj() == null) {
//						return json;
//					}
//				}
//				
//				
//
//			} else {
//				map.put("msg", "false");
//				application.setStatus(2); // 已驳回
//				application.setView(remark);
//				HyOrder order = hyOrderService.find(application.getOrderId());
//				order.setRefundstatus(4); // 退款已驳回
//				//order.setStatus(5);
//				hyOrderService.update(order);
//
//			}
//			Authentication.setAuthenticatedUserId(username);
////			taskService.addComment(task.getId(), processInstanceId,
////					(comment == null ? "审核通过" : comment) + ":" + auditStatus);
////			taskService.complete(task.getId(), map);
//			hyOrderApplicationService.update(application);
//			json.setSuccess(true);
//			json.setMsg("审核成功");
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setSuccess(false);
//			json.setMsg("审核失败");
//			e.printStackTrace();
//		}
//		return json;
//	}
	
	
	/**
	 * 订单退款条目列表
	 * @author LBC
	 */
	@RequestMapping(value = "order_refund/item_list")
	@ResponseBody
	public Json OrderRefundItemList(Long id) {
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}
			
			List<HyOrderItem> lists = new ArrayList<>();
			for (HyOrderItem item : order.getOrderItems()) {
				if(item.getStatus() == 0) {

					lists.add(item);
				}
			}
			Map<String, Object> map = new HashMap<>();
			
			map.put("hyOrderItems", lists);
			map.put("orderId", id);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}


	/**
	 * 收退款记录
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "payandrefund_record/list")
	@ResponseBody
	public Json payAndRefundList(Long id, Integer type) {
		Json json = new Json();
		try {

			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}

			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", id));
			filters.add(Filter.eq("type", type));

			List<PayandrefundRecord> records = payandrefundRecordService.findList(null, filters, null);

			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(records);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
		}
		return json;
	}

	/**
	 * 调整金额
	 * 
	 * @param id
	 * @param adjustMoney
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "adjust_money")
	@ResponseBody
	public Json adjustMoney(Long id, BigDecimal adjustMoney, HttpSession session) {
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单无效");
			}
			if (!order.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_STORE_PAY)) {
				throw new Exception("订单状态不对");
			}
			BigDecimal oldAjustMoney = order.getAdjustMoney();
			if(oldAjustMoney==null){
				oldAjustMoney=BigDecimal.valueOf(0);
			}
			//修改订单金额
			order.setWaimaiMoney(order.getWaimaiMoney().subtract(oldAjustMoney).add(adjustMoney));
			order.setJiusuanMoney(order.getJiusuanMoney().subtract(oldAjustMoney).add(adjustMoney));
			//order.setJiesuanMoney1(order.getJiesuanMoney1().subtract(oldAjustMoney).add(adjustMoney));
			order.setAdjustMoney(adjustMoney);
			
			
			
			hyOrderService.update(order);
			json.setSuccess(true);
			json.setMsg("调整金额成功");

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("调整金额失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	//线程不安全
	//Collections.synchronizedSet(new HashSet())
	static Set<Long> orderIdSet = new HashSet<>();

	/**
	 * 订单支付
	 * @author LBC
	 */
	@RequestMapping(value = "pay")
	@ResponseBody
	public Json pay(Long id, HttpSession session) {
		Json json = new Json();
		HyOrder hyOrder = null;
		try {
			if(orderIdSet.contains(id)) {
				json.setSuccess(false);
				json.setMsg("订单正在支付 ");
				return json;
			}
			else {
				orderIdSet.add(id);
				hyOrder = hyOrderService.find(id);
				
				//已支付
				//synchronized(hyOrder) {
					//已支付过
				if(hyOrder.getPaystatus().equals(1)) {
					json.setSuccess(false);
					orderIdSet.remove(id);
					json.setMsg("订单已经支付过 ");
					return json;
				}
				
				json = hyOrderService.addInsuranceOrderPayment(id, session);
				if(json.getMsg().contains("预存款不足")) {
					orderIdSet.remove(id);
					return json;
				}
				//未支付过的话 状态变为已支付
				//hyOrder.setPaystatus(1);
				Long[] orderIds = new Long[1];
				orderIds[0] = id;
				json = insuranceOrderService.postOrderToJT(orderIds);
				//if(((List<Map<String,Object>>)((Map<String,Object>)json.getObj()).get("successIds")).length())
				if(json.getObj() == null) {
					//hyOrder.setPaystatus(0);
					//hyOrderService.update(hyOrder);
					json.setSuccess(false);
					json.setMsg("下单到江泰失败,请到保单管理手动投保 ");
					orderIdSet.remove(id);
					return json;
				}
				Map<String,Object> map = (Map<String,Object>)json.getObj();
				List<Map<String,Object>> list = (List<Map<String,Object>>) (map.get("successIds"));
				//下单到江泰失败
				if(list.size() < 1) {
					//set为未支付
					//hyOrder.setPaystatus(0);
					//hyOrderService.update(hyOrder);
					json.setSuccess(false);
					json.setMsg("下单到江泰失败,请到保单管理手动投保 ");
					orderIdSet.remove(id);
					return json;
				}
					
				//json = hyOrderService.addInsuranceOrderPayment(id, session);
				
				orderIdSet.remove(id);
			}
			
			//}
		} catch (Exception e) {
			// TODO: handle exception
//			if(hyOrder != null) {
//				hyOrder.setPaystatus(0);
//			}
			orderIdSet.remove(id);
			json.setSuccess(false);
			json.setMsg("支付错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;

	}
//	
//	@RequestMapping(value = "order_pay/list/view")
//	@ResponseBody
//	public Json OrderPayList(Pageable pageable, Integer type, String name) {
//		Json json = new Json();
//		try {
//			List<Filter> filters = new ArrayList<>();
//			if(type != null) {
//				filters.add(Filter.eq("classify", type));
//			}
//			if(name != null) {
//				filters.add(Filter.like("remark", name));
//			}
//			List<Insurance> insurances = insuranceService.findList(null, filters, null);
//			
//			int pageNumber = pageable.getPage();
//			int pageSize = pageable.getRows();
//			Map<String, Object> hMap = new HashMap<>();
//			hMap.put("pageNumber", pageNumber);
//			hMap.put("pageSize", pageSize);
//			hMap.put("total", insurances.size());
//			hMap.put("rows", insurances.subList((pageNumber - 1) * pageSize,
//					pageNumber * pageSize > insurances.size() ? insurances.size() : pageNumber * pageSize));
//			json.setMsg("获取成功");
//			json.setSuccess(true);
//			json.setObj(hMap);
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setSuccess(false);
//			json.setMsg("获取列表失败");
//			json.setObj(e.getMessage());
//			e.printStackTrace();
//		}
//		return json;
//	}
//	
//	@RequestMapping(value = "order_pay/detail/view")
//	@ResponseBody
//	public Json OrderPayDetail(Pageable pageable, Long id) {
//		Json json = new Json();
//		try {
//			Insurance insurance = insuranceService.find(id);
//			Map<String, Object> map = new HashMap<String, Object>(); 
//			map.put("Insurance", insurance);
//			json.setMsg("获取成功");
//			json.setSuccess(true);
//			json.setObj(map);
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setSuccess(false);
//			json.setMsg("获取列表失败");
//			json.setObj(e.getMessage());
//			e.printStackTrace();
//		}
//		return json;
//	}
//	
//	static class WrapOrder{
//		private Long insurance_id;
//		private HyOrder hyOrder;
//		public Long getInsurance_id() {
//			return insurance_id;
//		}
//		public void setInsurance_id(Long insurance_id) {
//			this.insurance_id = insurance_id;
//		}
//		public HyOrder getHyOrder() {
//			return hyOrder;
//		}
//		public void setHyOrder(HyOrder hyOrder) {
//			this.hyOrder = hyOrder;
//		}
//		
//		
//		
//	}
//	
//	@RequestMapping(value = "order_pay/add_order")
//	@ResponseBody
//	public Json OrderPayAddOrder(HttpSession session, @RequestBody WrapOrder wrapOrder) {
//		Json json = new Json();
//		/**
//		 * 获取当前用户
//		 */
//		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin admin = hyAdminService.find(username);
//		try {
//			if(wrapOrder == null) {
//				json.setSuccess(false);
//				json.setMsg("未能收到前台数据");
//				return json;
//			}
//			if(wrapOrder.getHyOrder() == null) {
//				json.setSuccess(false);
//				json.setMsg("未能收到订单数据");
//				return json;
//			}
//			Store store = storeService.findStore(admin);
//			if(store == null) {
//				wrapOrder.getHyOrder().setContact(admin.getName());
//			}
//			else {
//				wrapOrder.getHyOrder().setContact(store.getStoreName() + admin.getName());
//			}
//			Insurance insurance = insuranceService.find(wrapOrder.getInsurance_id());
//			if(insurance == null) {
//				json.setSuccess(false);
//				json.setMsg("不存在此保险，请检查");
//				return json;
//			}
//			wrapOrder.getHyOrder().setInsurance(insurance);
//			wrapOrder.getHyOrder().setName(insurance.getRemark());
//			wrapOrder.getHyOrder().setXianlutype(insurance.getClassify());
//			
//			wrapOrder.getHyOrder().setPhone(admin.getMobile());
//			//调用service中的接口
//			json = hyOrderService.addInsuranceOrder(wrapOrder.getHyOrder(), session);
//			
//			
//			
////			Map<String, Object> map = new HashMap<String, Object>(); 
////			map.put("Insurance", insurance);
//			//json.setMsg("获取成功");
//			//json.setSuccess(true);
//			//json.setObj(null);
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setSuccess(false);
//			json.setMsg("获取列表失败");
//			json.setObj(e.getMessage());
//			e.printStackTrace();
//		}
//		return json;
//	}
//	
//	@RequestMapping(value = "order_pay/customer_information")
//	@ResponseBody
//	public Json CustomerInformation(HttpSession session, Integer type, String certificate) {
//		Json json = new Json();
//		try {
//			
//			if(type == null) {
//				json.setSuccess(false);
//				json.setMsg("未传证件类型");
//				return json;
//			}
//			if(certificate == null) {
//				json.setSuccess(false);
//				json.setMsg("未传证件号");
//				return json;
//			}
//			
//			List<Filter> filters = new ArrayList<>();
//			filters.add(Filter.eq("certificateType", type));
//			filters.add(Filter.eq("certificate", certificate));
//			
//			List<HyOrderCustomer> hyOrderCustomers = hyOrderCustomerService.findList(null, filters, null);
//			if(hyOrderCustomers.size() > 0) {
//				json.setMsg("获取成功");
//				json.setObj(hyOrderCustomers.get(0));
//			}
//			else {
//				json.setMsg("查无此人");
//				json.setObj(null);
//			}
//			
//			
////			Map<String, Object> map = new HashMap<String, Object>(); 
////			map.put("Insurance", insurance);
//			
//			json.setSuccess(true);
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setSuccess(false);
//			json.setMsg("获取列表失败");
//			json.setObj(e.getMessage());
//			e.printStackTrace();
//		}
//		return json;
//	}
//	
//	@RequestMapping(value = "order_pay/get_excel")
//	//@ResponseBody
//	public void GetExcel(HttpServletRequest request, HttpServletResponse response) {
////		response.setContentType("text/html;charset=utf-8");
////        response.setCharacterEncoding("utf-8");
//        try {
//        	//C:\Users\LBC\workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\hy_backend\download\投保人员信息表.xls
//        	String filefullname =System.getProperty("hongyu.webapp") + "download/投保人员信息表birthday.xls";
//            String fileName = "投保人员信息表.xls";
//			File file = new File(filefullname);
//			System.out.println(filefullname);
//			System.out.println(file.getAbsolutePath());
//			if (!file.exists()) {
//			    request.setAttribute("message", "下载失败");
//			    return;
//                
//            } else {
//
//                // 设置相应头，控制浏览器下载该文件，这里就是会出现当你点击下载后，出现的下载地址框
//                response.setHeader("content-disposition",
//                        "attachment;filename=" + URLEncoder.encode("投保人员信息表.xls", "utf-8"));
//                
//                
//               
//        		
//        		response.setHeader("content-disposition",
//        				"attachment;" + "filename=" + URLEncoder.encode(fileName, "UTF-8"));	
//        		
//        		response.setHeader("Connection", "close");
//        		response.setHeader("Content-Type", "application/vnd.ms-excel");
//
//        		//String zipfilefullname = userdir + zipFileName;
//        		FileInputStream fis = new FileInputStream(file);
//        		BufferedInputStream bis = new BufferedInputStream(fis);
//        		ServletOutputStream sos = response.getOutputStream();
//        		BufferedOutputStream bos = new BufferedOutputStream(sos);
//
//        		byte[] bytes = new byte[1024];
//        		int i = 0;
//        		while ((i = bis.read(bytes, 0, bytes.length)) != -1) {
//        			bos.write(bytes);
//        		}
//        		bos.flush();
//        		bis.close();
//        		bos.close();
//            }
//        }
//        catch (Exception e) {
//			// TODO: handle exception
//        	request.setAttribute("message", "出现错误");
//            e.printStackTrace();
//		}
//        return;
//		
//	}
//	
//	@RequestMapping(value = "order_pay/upload_excel")
//	@ResponseBody
//	public Json UploadExcel(@RequestParam MultipartFile[] files) {
//        Json json = new Json();
//		try {
//			if(files == null || files[0] == null) {
//				json.setMsg("未接受到文件");
//	        	json.setSuccess(false);
//	        	json.setObj(null);
//			}
//			MultipartFile file = files[0];
//			
////        	String localfileName = "WebRoot/upload/投保人员信息表.xls";
////        	File localfile = new File(localfileName);
////        	file.transferTo(localfile);
//        	List<Member> members = MoBanExcelUtil.readExcelBirthday(file.getInputStream());
//        	List<Map<String,Object> > list = new ArrayList<>();
//            for(Member member : members) {
//            	Map<String, Object> map = new HashMap<>();
//            	map.put("name", member.getName());
//            	map.put("certificateType", member.getCertificateType());
//            	map.put("certificate", member.getCertificateNumber());
//            	map.put("age", member.getAge());
//            	map.put("gender", member.getSex());
//            	//map.put("phone", member.getPhone());
//            	list.add(map);
//            }
//        	
//			json.setObj(list);
//			json.setMsg("文件读取成功");
//			json.setSuccess(true);
//			//删除excel
////			if(!localfile.exists()) {
////				//
////			}
////			else {
////				if(localfile.isFile()) {
////					localfile.delete();
////				}
////			}
//
//        }
//        catch (Exception e) {
//			// TODO: handle exception
//        	json.setMsg("文件读取失败");
//        	json.setSuccess(false);
//        	json.setObj(null);
//            
//		}
//		return json;
//       
//		
//	}
//	
//	@RequestMapping(value = "order_pay/upload_excel_1")
//	@ResponseBody
//	public Json UploadExcel(@RequestParam MultipartFile file) {
//        Json json = new Json();
//		try {
//			if(file == null) {
//				json.setMsg("未接受到文件");
//	        	json.setSuccess(false);
//	        	json.setObj(null);
//			}
//			
////        	String localfileName = "WebRoot/upload/投保人员信息表111.xls";
////        	//String localfileName = System.getProperty("java.io.tmpdir") + "/upload_" + UUID.randomUUID() + ".xls";
////        	File localfile = new File(localfileName);
////        	file.transferTo(localfile);
////        	System.out.println(localfileName);
////        	System.out.println(localfile.getAbsolutePath());
////
////        	
////        	//List<Member> members = MoBanExcelUtil.readExcel(localfileName);
//        	List<Member> members = MoBanExcelUtil.readExcelBirthday(file.getInputStream());
//        	List<Map<String,Object> > list = new ArrayList<>();
//            for(Member member : members) {
//            	Map<String, Object> map = new HashMap<>();
//            	map.put("name", member.getName());
//            	map.put("certificateType", member.getCertificateType());
//            	map.put("certificate", member.getCertificateNumber());
//            	map.put("age", member.getAge());
//            	map.put("gender", member.getSex());
//            	//map.put("phone", member.getPhone());
//            	list.add(map);
//            }
//        	
//			json.setObj(list);
//			json.setMsg("文件读取成功");
//			json.setSuccess(true);
//			//删除excel
////			if(!localfile.exists()) {
////				//
////			}
////			else {
////				if(localfile.isFile()) {
////					localfile.delete();
////				}
////			}
//
//        }
//        catch (Exception e) {
//			// TODO: handle exception
//        	json.setMsg("文件读取失败");
//        	json.setSuccess(false);
//        	json.setObj(null);
//            
//		}
//		return json;
//       
//		
//	}
	
	
	@RequestMapping(value = "qkcz/list")
	@ResponseBody
	public Json qkcz(Pageable pageable,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,HttpSession session){
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin liable = hyAdminService.find(username);
		
		List<Filter> filters = new ArrayList<>();
//		filters.add(Filter.eq("liable", liable));	
//		List<HySupplierContract> supplierContracts = hySupplierContractService.findList(null, filters, null);
//		HySupplierContract contract = supplierContracts.get(0);
//		Long supplierId = contract.getHySupplier().getId();
		
		try{
//			filters.clear();
			if(startDate!=null){
				filters.add(Filter.ge("date", startDate));
			}
			if(endDate!=null){
				filters.add(Filter.le("date", endDate));
			}
			filters.add(Filter.eq("supplierName",username));
			filters.add(Filter.eq("state", 0));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("date"));
			pageable.setOrders(orders);
			Page<ReceiptServicer> page=receiptServicerService.findPage(pageable);
			List<Map<String, Object>> result=new LinkedList<>();
			for(ReceiptServicer receiptServicer:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				map.put("operator",receiptServicer.getOperator());
				map.put("amount", receiptServicer.getAmount());
				map.put("balance", receiptServicer.getBalance());
				map.put("date", receiptServicer.getDate());
				HyOrder hyOrder = hyOrderService.find(receiptServicer.getOrderOrPayServicerId());
//				HyGroup group = hyGroupService.find(hyOrder.getGroupId());
				map.put("orderNumber",hyOrder.getOrderNumber());
//				map.put("xianlumingcheng",hyOrder.getXianlumingcheng());
				map.put("orderName",hyOrder.getName());
//				map.put("group",group.getGroupLineName());
				result.add(map);
			}
			Map<String, Object> hMap=new HashMap<>();
			hMap.put("total", page.getTotal());
			hMap.put("pageNumber", page.getPageNumber());
			hMap.put("pageSize", page.getPageSize());
			hMap.put("rows", result);
			json.setObj(hMap);
			json.setMsg("操作成功");
			json.setSuccess(true);
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("操作失败");
			json.setSuccess(false);
		}
	return json;
	}
	
	@RequestMapping(value = "qkzc/list")
	@ResponseBody
	public Json qkzc(Pageable pageable,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,HttpSession session){
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin liable = hyAdminService.find(username);
		
		List<Filter> filters = new ArrayList<>();
//		filters.add(Filter.eq("liable", liable));	
//		List<HySupplierContract> supplierContracts = hySupplierContractService.findList(null, filters, null);
//		HySupplierContract contract = supplierContracts.get(0);
//		Long supplierId = contract.getHySupplier().getId();
		try{
//			filters.clear();
			if(startDate!=null){
				filters.add(Filter.ge("date", startDate));
			}
			if(endDate!=null){
				filters.add(Filter.le("date", endDate));
			}
			filters.add(Filter.eq("supplierName",username));
			filters.add(Filter.eq("state", 1));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("date"));
			pageable.setOrders(orders);
			Page<ReceiptServicer> page=receiptServicerService.findPage(pageable);
			List<Map<String, Object>> result=new LinkedList<>();
			for(ReceiptServicer receiptServicer:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				map.put("operator",receiptServicer.getOperator());
				map.put("amount", receiptServicer.getAmount());
				map.put("balance", receiptServicer.getBalance());
				map.put("date", receiptServicer.getDate());
				PayServicer payServicer = payServicerService.find(receiptServicer.getOrderOrPayServicerId());
//				map.put("payer",payServicer.getPayer());
				map.put("confirmCode", payServicer.getConfirmCode());
				result.add(map);
			}
			Map<String, Object> hMap=new HashMap<>();
			hMap.put("total", page.getTotal());
			hMap.put("pageNumber", page.getPageNumber());
			hMap.put("pageSize", page.getPageSize());
			hMap.put("rows", result);
			json.setObj(hMap);
			json.setMsg("操作成功");
			json.setSuccess(true);
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("操作失败");
			json.setSuccess(false);
		}
	return json;
		
	}
	
	@RequestMapping("balance/view")
	@ResponseBody
	public Json getBalance(HttpSession session){
		Json json=new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin liable = hyAdminService.find(username);
		
		List<Filter> filters = new ArrayList<>();
//		filters.add(Filter.eq("liable", liable));	
//		List<HySupplierContract> supplierContracts = hySupplierContractService.findList(null, filters, null);
//		HySupplierContract contract = supplierContracts.get(0);
//		Long supplierId = contract.getHySupplier().getId();
		
		try {
//			filters.clear();
			filters.add(Filter.eq("supplierName",username));
			List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null,filters,null);
			BigDecimal balance = new BigDecimal(0);
			if(receiptTotalServicers.size()!=0){
				balance = balance.add(receiptTotalServicers.get(0).getBalance());
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(balance);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
		}
		return json;
	}
	@RequestMapping("getStoreType/view")
	@ResponseBody
	public Json getStoreType(HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Store store = storeService.findStore(hyAdmin);
			if(store==null){
				json.setSuccess(false);
				json.setMsg("门店不存在");
			}else{
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(store.getStoreType());
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
	}
	
	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@RequestMapping("bank/list/view")
	@ResponseBody
	public Json bankList(HttpSession session) {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		try {
			List<Filter> branchFilters = new  ArrayList<>();
			
			branchFilters.add(Filter.eq("hyDepartment", EmployeeUtil.getCompany(hyAdminService.find(username))));
			List<HyCompany> hyCompanys = hyCompanyService.findList(null,branchFilters,null);
			HyCompany hyCompany2 = null;
			Set<BankList> branchbankLists = null;
			if(hyCompanys.size()!=0&&!hyCompanys.isEmpty()){
				hyCompany2 =  hyCompanys.get(0);
				branchbankLists  = hyCompany2.getBankLists();
			}
			
			List<HashMap<String, Object>> res2 = new ArrayList<>();
			for(BankList branchbankList :branchbankLists){
				HashMap<String, Object> m = new HashMap<>();
				m.put("id", branchbankList.getId());
				m.put("branchAccountAlias", branchbankList.getAlias());
				m.put("branchBankName", branchbankList.getBankName());
				m.put("branchBankCode", branchbankList.getBankCode());
				m.put("branchBankType", branchbankList.getBankType());
				m.put("bankNum", branchbankList.getBankAccount());
				res2.add(m);
			}
			
			json.setObj(res2);
			json.setSuccess(true);
			json.setMsg("获取成功");
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("获取失败");
			json.setObj(e);
		}
		return json;
	}
}
