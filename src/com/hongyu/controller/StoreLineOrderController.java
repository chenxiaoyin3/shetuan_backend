package com.hongyu.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.hongyu.entity.*;
import com.hongyu.service.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.ListenerManager;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplierContract.Settle;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.liyang.EmployeeUtil;

@Controller
@RequestMapping("/admin/storeLineOrder/")
public class StoreLineOrderController {
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
	
	@Resource(name = "fddContractServiceImpl")
	FddContractService fddContractService;
	
	@Resource(name = "fddDayTripContractServiceImpl")
	FddDayTripContractService fddDayTripContractService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	
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
	
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime, HttpSession session, 
			HttpServletRequest request, Integer paystatus, Integer checkstatus, Integer contractStatus,
			Integer refundstatus, Integer source, String orderNumber, String name,Integer status) {
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
			hyOrder.setCheckstatus(checkstatus);
			hyOrder.setRefundstatus(refundstatus);
			hyOrder.setSource(source);
			hyOrder.setOrderNumber(orderNumber);
			hyOrder.setName(name);

			//hyOrder.setStatus(status);

			/**
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("type", 1));
			filters.add(Filter.in("operator", hyAdmins));

			filters.add(Filter.eq("paystatus",paystatus));
			filters.add(Filter.eq("checkstatus",checkstatus));
			filters.add(Filter.eq("refundstatus",refundstatus));
			filters.add(Filter.eq("source",source));
			filters.add(Filter.eq("orderNumber",orderNumber));
			filters.add(Filter.eq("name",name));
			
			if(startTime != null) {
				filters.add(Filter.ge("fatuandate", startTime));
			}
			if(endTime != null) {
				filters.add(Filter.le("fatuandate", endTime));
			}
			
			if(status!=null) {
				switch(status) {
				case 0:{
					filters.add(Filter.lt("status", 3));
				}break;
				case 1:{
					filters.add(Filter.eq("status", 3));
				}break;
				case 2:{
					filters.add(Filter.gt("status", 3));
					filters.add(Filter.lt("status", 6));
				}break;
				case 3:{
					filters.add(Filter.eq("status", 6));
				}break;
				default:break;
				}
				hyOrder.setCheckstatus(null);
			}
//			if(contractStatus!=null){
//				if(contractStatus==1){
//					filters.add(Filter.isNotNull("contractId"));
//				}else{
//					filters.add(Filter.isNull("contractId"));
//				}
//			}
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createtime"));
			List<HyOrder> hyOrders = hyOrderService.findList(null,filters,orders);
			List<Map<String, Object>> result = new ArrayList<>();
			for (HyOrder tmp : hyOrders) {
				if(contractStatus!=null) {
					if(contractStatus!=0 && tmp.getContractId()==null)
						continue;						
					if(tmp.getContractId()!=null){
						if(tmp.getContractType()==0){
							//是一日游合同
							FddDayTripContract fddDayTripContract = fddDayTripContractService.find(tmp.getContractId());
							if(contractStatus==0){
								//未签署
								if(fddDayTripContract.getStatus()==4){
									continue;
								}
							}
							if(contractStatus==1){
								//已签署
								if(fddDayTripContract.getStatus()!=4){
									continue;
								}
							}
							if(contractStatus==2){
								//以作废
								if(fddDayTripContract.getStatus()!=5){
									continue;
								}
							}
							if(contractStatus==3){
								if(fddDayTripContract.getStatus()!=6)
									continue;
							}			
						}else{
							FddContract fddContract = fddContractService.find(tmp.getContractId());				
							if(contractStatus==0){
								//未签署
								if(fddContract.getStatus()==4){
									continue;
								}
							}
							if(contractStatus==1){
								//已签署
								if(fddContract.getStatus()!=4){
									continue;
								}
							}
							if(contractStatus==2){
								//以作废
								if(fddContract.getStatus()!=5){
									continue;
								}
							}
							if(contractStatus==3){
								if(fddContract.getStatus()!=6)
									continue;
							}			
						}
						
					}	
				}
				Map<String, Object> map = new HashMap<>();
				HyAdmin operator = tmp.getOperator();
				map.put("id", tmp.getId());
				map.put("status", tmp.getStatus());
				map.put("orderNumber", tmp.getOrderNumber());
				map.put("name", tmp.getName());
				map.put("source", tmp.getSource());
				int source1 = tmp.getSource();
				if(source1 != Constants.mendian){
					map.put("storeName", "");
				}else{
					Long storeId = tmp.getStoreId();
					Store store = storeService.find(storeId);
					map.put("storeName", store==null?"":store.getStoreName());
				}
				map.put("people", tmp.getPeople());
				map.put("jiusuanMoney", tmp.getJiusuanMoney());
				map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
				map.put("storeFanli",tmp.getStoreFanLi());
				map.put("tip",tmp.getTip());
				map.put("discountedPrice",tmp.getDiscountedPrice());

				map.put("contractId", tmp.getContractId());
				//合同类型   0--国内一日游  1--国内游  2--境外游
				Integer contractType = 0;
				if(tmp.getXianlutype()!=null){
					if(tmp.getXianlutype()>3){
						//大于3即为出境类型
						contractType = 2;
					}else{
						if(tmp.getTianshu()>1){
							contractType = 1;
						}else{
							contractType = 0;
						}
					}
				}
				map.put("contractType",contractType);
				map.put("contractNumber", tmp.getContractNumber());
				map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
				
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
				Long groupId = tmp.getGroupId();
				if(groupId!=null){
					HyGroup hyGroup = hyGroupService.find(groupId);
					if(hyGroup==null)
						throw new Exception("线路订单中id为"+tmp.getId()+"中的团id"+groupId+" 对应的团为null");
					map.put("linePn", hyGroup.getGroupLinePn());
					HyLine hyLine = hyGroup.getLine();
					map.put("provider", hyLine.getHySupplier());	//供应商信息
				}
				map.put("supplierName", tmp.getSupplier().getName());
				map.put("fatuandate", tmp.getFatuandate());
				map.put("discountedPrice", tmp.getDiscountedPrice());
				
				
				List<Filter> insurancefilters = new ArrayList<>();
				insurancefilters.add(Filter.eq("orderId", tmp.getId()));
				insurancefilters.add(Filter.eq("status", 3));
				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,insurancefilters,null);
				if(insuranceOrders!=null && !insuranceOrders.isEmpty()) {
					InsuranceOrder insuranceOrder = insuranceOrders.get(0);
					map.put("jtPolicyNo", insuranceOrder.getJtPolicyNo());
				}
				
				result.add(map);
			}
			Map<String, Object> hMap = new HashMap<>();
			int page = pageable.getPage();
			int rows = pageable.getRows();
			hMap.put("pageNumber", page);
			hMap.put("pageSize", rows);
			hMap.put("total", result.size());
			hMap.put("rows", result.subList((page - 1) * rows, page * rows > result.size() ? result.size() : page * rows));
//			pageable.setOrders(orders);
//			pageable.setFilters(filters);
//			Page<HyOrder> page = hyOrderService.findPage(pageable, hyOrder);
//			List<Map<String, Object>> result = new LinkedList<>();
//			for (HyOrder tmp : page.getRows()) {
//				Map<String, Object> map = new HashMap<>();
//				HyAdmin operator = tmp.getOperator();
//				map.put("id", tmp.getId());
//				map.put("status", tmp.getStatus());
//				map.put("orderNumber", tmp.getOrderNumber());
//				map.put("name", tmp.getName());
//				map.put("source", tmp.getSource());
//				int source1 = tmp.getSource();
//				if(source1 != Constants.mendian){
//					map.put("storeName", "");
//				}else{
//					Long storeId = tmp.getStoreId();
//					Store store = storeService.find(storeId);
//					map.put("storeName", store==null?"":store.getStoreName());
//				}
//				map.put("people", tmp.getPeople());
//				map.put("jiusuanMoney", tmp.getJiusuanMoney());
//				map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
//				map.put("contractId", tmp.getContractId());
//				//合同类型   0--国内一日游  1--国内游  2--境外游
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
//				map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
//				
//				map.put("createtime", tmp.getCreatetime());
//				if (operator.equals(admin)) {
//					if (co == CheckedOperation.view) {
//						map.put("privilege", "view");
//					} else {
//						map.put("privilege", "edit");
//					}
//				} else {
//					if (co == CheckedOperation.edit) {
//						map.put("privilege", "edit");
//					} else {
//						map.put("privilege", "view");
//					}
//				}
//				
////				Long storeId = tmp.getStoreId();
////				if(storeId!=null){
////					Store store = storeService.find(storeId);
////					map.put("storeName", store==null?"":store.getStoreName());
////				}else{
////					map.put("storeName", "");
////				}
//				Long groupId = tmp.getGroupId();
//				if(groupId!=null){
//					HyGroup hyGroup = hyGroupService.find(groupId);
//					map.put("linePn", hyGroup.getGroupLinePn());
//					HyLine hyLine = hyGroup.getLine();
//					map.put("provider", hyLine.getHySupplier());	//供应商信息
//				}
//				map.put("supplierName", tmp.getSupplier().getName());
//				map.put("fatuandate", tmp.getFatuandate());
//				map.put("discountedPrice", tmp.getDiscountedPrice());
//				
//				result.add(map);
//			}
//			Map<String, Object> hMap = new HashMap<>();
//			hMap.put("pageNumber", page.getPageNumber());
//			hMap.put("pageSize", page.getPageSize());
//			hMap.put("total", page.getTotal());
//			hMap.put("rows", result);
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



	@RequestMapping("mh/list/view")
	@ResponseBody
	public Json mhList(Pageable pageable, @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime,
	                 @DateTimeFormat(pattern="yyyy-MM-dd") Date endTime, HttpSession session,
	                 HttpServletRequest request, Integer paystatus, Integer checkstatus, Integer contractStatus,
	                 Integer refundstatus, Integer source, String orderNumber, String name,Integer status) {
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
			hyOrder.setCheckstatus(checkstatus);
			hyOrder.setRefundstatus(refundstatus);
			if(source==null){
				hyOrder.setSource(1);
			}else{
				hyOrder.setSource(source);
			}
			hyOrder.setOrderNumber(orderNumber);
			hyOrder.setName(name);

			//hyOrder.setStatus(status);

			/**
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
//			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);

			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("type", 1));
//			filters.add(Filter.in("operator", hyAdmins));

			if(startTime != null) {
				filters.add(Filter.ge("fatuandate", startTime));
			}
			if(endTime != null) {
				filters.add(Filter.le("fatuandate", endTime));
			}

			if(status!=null) {
				switch(status) {
					case 0:{
						filters.add(Filter.lt("status", 3));
					}break;
					case 1:{
						filters.add(Filter.eq("status", 3));
					}break;
					case 2:{
						filters.add(Filter.gt("status", 3));
						filters.add(Filter.lt("status", 6));
					}break;
					case 3:{
						filters.add(Filter.eq("status", 6));
					}break;
					default:break;
				}
				hyOrder.setCheckstatus(null);
			}
			if(contractStatus!=null){
				if(contractStatus==1){
					filters.add(Filter.isNotNull("contractId"));
				}else{
					filters.add(Filter.isNull("contractId"));
				}
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
				map.put("source", tmp.getSource());
				int source1 = tmp.getSource();
				if(source1 != Constants.mendian){
					map.put("storeName", "");
				}else{
					Long storeId = tmp.getStoreId();
					Store store = storeService.find(storeId);
					map.put("storeName", store==null?"":store.getStoreName());
				}
				map.put("people", tmp.getPeople());
				map.put("jiusuanMoney", tmp.getJiusuanMoney());
				map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
				map.put("storeFanli",tmp.getStoreFanLi());
				map.put("tip",tmp.getTip());
				map.put("discountedPrice",tmp.getDiscountedPrice());
				map.put("contractId", tmp.getContractId());
				//合同类型   0--国内一日游  1--国内游  2--境外游
				Integer contractType = 0;
				if(tmp.getXianlutype()!=null){
					if(tmp.getXianlutype()>3){
						//大于3即为出境类型
						contractType = 2;
					}else{
						if(tmp.getTianshu()>1){
							contractType = 1;
						}else{
							contractType = 0;
						}
					}
				}
				map.put("contractType",contractType);
				map.put("contractNumber", tmp.getContractNumber());
				map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息

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

//				Long storeId = tmp.getStoreId();
//				if(storeId!=null){
//					Store store = storeService.find(storeId);
//					map.put("storeName", store==null?"":store.getStoreName());
//				}else{
//					map.put("storeName", "");
//				}
				Long groupId = tmp.getGroupId();
				if(groupId!=null){
					HyGroup hyGroup = hyGroupService.find(groupId);
					map.put("linePn", hyGroup.getGroupLinePn());
					HyLine hyLine = hyGroup.getLine();
					map.put("provider", hyLine.getHySupplier());	//供应商信息
				}
				map.put("supplierName", tmp.getSupplier().getName());
				map.put("fatuandate", tmp.getFatuandate());
				map.put("discountedPrice", tmp.getDiscountedPrice());

				List<Filter> insurancefilters = new ArrayList<>();
				insurancefilters.add(Filter.eq("orderId", tmp.getId()));
				insurancefilters.add(Filter.eq("status", 3));
				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,insurancefilters,null);
				if(insuranceOrders!=null && !insuranceOrders.isEmpty()) {
					InsuranceOrder insuranceOrder = insuranceOrders.get(0);
					map.put("jtPolicyNo", insuranceOrder.getJtPolicyNo());
				}
				
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


	@RequestMapping("gys/list/view")
	@ResponseBody
	public Json gysList(Pageable pageable, HyOrder hyOrder, HttpSession session,HttpServletRequest request) {
		Json json = new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			/**
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("type", 1));
			filters.add(Filter.in("supplier", hyAdmins));
			if(hyOrder.getCheckstatus()!=null) {
				switch(hyOrder.getCheckstatus()) {
				case 0:{
					filters.add(Filter.lt("status", 3));
				}break;
				case 1:{
					filters.add(Filter.eq("status", 3));
				}break;
				case 2:{
					filters.add(Filter.gt("status", 3));
					filters.add(Filter.lt("status", 6));
				}break;
				case 3:{
					filters.add(Filter.eq("status", 6));
				}break;
				default:break;
				}
				hyOrder.setCheckstatus(null);				
			}

			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createtime"));
			pageable.setOrders(orders);
			
			pageable.setFilters(filters);
			Page<HyOrder> page = hyOrderService.findPage(pageable, hyOrder);
			List<Map<String, Object>> result = new LinkedList<>();
			for (HyOrder tmp : page.getRows()) {
				Map<String, Object> map = new HashMap<>();
				HyAdmin supplier = tmp.getSupplier();
				map.put("id", tmp.getId());
				map.put("status", tmp.getStatus());
				map.put("orderNumber", tmp.getOrderNumber());
				map.put("name", tmp.getName());
				map.put("source", tmp.getSource());
				map.put("people", tmp.getPeople());
				map.put("jiusuanMoney", tmp.getJiusuanMoney());
				map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
				map.put("storeFanli",tmp.getStoreFanLi());
				map.put("tip",tmp.getTip());
				map.put("discountedPrice",tmp.getDiscountedPrice());
				map.put("contractId", tmp.getContractId());
				map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
				map.put("createtime", tmp.getCreatetime());
				if (supplier.equals(admin)) {
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
					HyLine hyLine = hyGroup.getLine();
					map.put("provider", hyLine.getHySupplier());	//供应商信息
				}
				map.put("fatuandate", tmp.getFatuandate());
				map.put("discountedPrice", tmp.getDiscountedPrice());
				
				List<Filter> insurancefilters = new ArrayList<>();
				insurancefilters.add(Filter.eq("orderId", tmp.getId()));
				insurancefilters.add(Filter.eq("status", 3));
				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,insurancefilters,null);
				if(insuranceOrders!=null && !insuranceOrders.isEmpty()) {
					InsuranceOrder insuranceOrder = insuranceOrders.get(0);
					map.put("jtPolicyNo", insuranceOrder.getJtPolicyNo());
				}
				
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

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			HyOrder tmp = hyOrderService.find(id);
			if(tmp==null)
				throw new NullPointerException("找不到对应的订单");
			Map<String, Object> map = new HashMap<>();	
			map.put("id", tmp.getId());
			map.put("orderNumber", tmp.getOrderNumber());
			map.put("name", tmp.getName());
			map.put("status", tmp.getStatus());
			map.put("paystatus", tmp.getPaystatus());
			map.put("checkstatus", tmp.getCheckstatus());
			map.put("guideCheckStatus", tmp.getGuideCheckStatus());
			map.put("refundstatus", tmp.getRefundstatus());
			map.put("type", tmp.getType());
			map.put("source", tmp.getSource());
			map.put("people", tmp.getPeople());
			map.put("storeType", tmp.getStoreType());
			map.put("storeId", tmp.getStoreId());
			Long storeId = tmp.getStoreId();
			if(storeId!=null){
				Store store = storeService.find(storeId);
				map.put("storeName", store==null?"":store.getStoreName());
			}else{
				map.put("storeName", "");
			}
			map.put("operator", tmp.getOperator());
			map.put("creatorId", tmp.getCreatorId());
			map.put("adjustMoney", tmp.getAdjustMoney());
			map.put("discountedType", tmp.getDiscountedType());
			map.put("discountedId", tmp.getDiscountedId());
			map.put("discountedPrice", tmp.getDiscountedPrice());
			map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
			map.put("jiusuanMoney", tmp.getJiusuanMoney());
			map.put("waimaiMoney", tmp.getWaimaiMoney());
			map.put("jiesuanTuikuan", tmp.getJiesuanTuikuan());
			map.put("waimaiTuikuan", tmp.getWaimaiTuikuan());
			map.put("baoxianJiesuanTuikuan", tmp.getBaoxianJiesuanTuikuan());
			map.put("baoxianWaimaiTuikuan", tmp.getBaoxianWaimaiTuikuan());
			map.put("storeFanLi", tmp.getStoreFanLi());
			map.put("ifjiesuan", tmp.getIfjiesuan());
			map.put("insuranceOrderDownloadUrl", tmp.getInsuranceOrderDownloadUrl());
			map.put("koudianMethod", tmp.getKoudianMethod());
			map.put("proportion", tmp.getProportion());
			map.put("headProportion", tmp.getHeadProportion());
			map.put("koudianMoney", tmp.getKoudianMoney());
			map.put("departure", tmp.getDeparture());
			map.put("fatuandate", tmp.getFatuandate());
			map.put("tianshu", tmp.getTianshu());
			map.put("huituanxinxi", tmp.getHuituanxinxi());
			map.put("xianlumingcheng", tmp.getXianlumingcheng());
			map.put("xianlutype", tmp.getXianlutype());
			map.put("fuwutype", tmp.getFuwutype());
			map.put("xingchenggaiyao", tmp.getXingchenggaiyao());
			map.put("tip", tmp.getTip());
			map.put("tipInstruction", tmp.getTipInstruction());
			map.put("contact", tmp.getContact());
			map.put("contactIdNumber", tmp.getContactIdNumber());
			map.put("phone", tmp.getPhone());
			map.put("remark", tmp.getRemark());
			map.put("contractId", tmp.getContractId());
			
			map.put("contractNumber", tmp.getContractNumber());
			map.put("contractType", tmp.getContractType());
			map.put("createtime", tmp.getCreatetime());
			map.put("modifytime", tmp.getModifytime());
			
			map.put("orderItems", tmp.getOrderItems());
			map.put("groupId", tmp.getGroupId());
			map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
			map.put("isDivideStatistic", tmp.getIsDivideStatistic());
			
			//查找额外保险信息
			Insurance insurance = insuranceService.getExtraInsuranceOfOrder(tmp);
			if(insurance!=null){

				Integer days = tmp.getTianshu();
				
				map.put("insurance",insurance.getRemark());
				
				for(InsurancePrice price:insurance.getInsurancePrices()){
					if(days.compareTo(price.getStartDay())>=0&&days.compareTo(price.getEndDay())<=0){
						map.put("insuranceMoney", price.getSalePrice());
						break;
					}
				}
				if(!map.containsKey("insuranceMoney")){
					throw new Exception("获取保险价格失败");
				}
			}
			Long groupId = tmp.getGroupId();
			if(groupId!=null){
				HyGroup hyGroup = hyGroupService.find(groupId);
				map.put("linePn", hyGroup.getGroupLinePn());
				HyLine hyLine = hyGroup.getLine();
				map.put("provider", hyLine.getHySupplier());	//供应商信息
				map.put("rebate", hyGroup.getFanliMoney()==null?0:hyGroup.getFanliMoney());
			}
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

	@RequestMapping("productList/view")
	@ResponseBody
	public Json productList(Pageable pageable, Long id) {
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			List<Map<String, Object>> result = new LinkedList<>();
			for (HyOrderItem tmp : hyOrder.getOrderItems()) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", tmp.getId());
				HyGroup hyGroup = hyGroupService.find(tmp.getProductId());
				map.put("productId", hyGroup.getGroupLinePn());
				map.put("name", tmp.getName());
				map.put("type", tmp.getType());
				map.put("number", tmp.getNumber());
				map.put("numberOfReturn", tmp.getNumberOfReturn());
				map.put("jiesuanPrice", tmp.getJiesuanPrice());
				map.put("waimaiPrice", tmp.getWaimaiPrice());
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
				if(hyOrderApplication.getOperator()!=null) {
					map.put("operator", hyOrderApplication.getOperator().getName());
				}else {
					map.put("operator", "");
				}
				
				map.put("orderId", hyOrderApplication.getOrderId());
				map.put("orderNumber", hyOrderApplication.getOrderNumber());
				map.put("outcome", hyOrderApplication.getOutcome());
				map.put("processInstanceId", hyOrderApplication.getProcessInstanceId());
				map.put("status", hyOrderApplication.getStatus());
				map.put("type", hyOrderApplication.getType());
				map.put("view", hyOrderApplication.getView());
				map.put("waimaiMoney", hyOrderApplication.getWaimaiMoney());
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

	// 取消线路订单
	@RequestMapping(value = "store_cancel")
	@ResponseBody
	public Json cancel(Long id, HttpSession session) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);

		Json json = new Json();
		try {
			
			hyOrderService.cancelOrder(id);
			HyOrderApplication application = new HyOrderApplication();
			application.setContent("门店取消订单");
			application.setOperator(admin);
			application.setOrderId(id);
			application.setCreatetime(new Date());
			application.setStatus(HyOrderApplication.STATUS_ACCEPT);
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

	// 门店确认线路订单
	@RequestMapping(value = "store_confirm")
	@ResponseBody
	public Json storeConfirm(Long id, String view, Integer status, HttpSession session) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}
			if (!order.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_STORE_CONFIRM)) {
				throw new Exception("订单状态不对");
			}

			if (status.equals(0)) {
				if (view == null || view.equals("")) {
					throw new Exception("驳回意见必填");
				}
				order.setStatus(Constants.HY_ORDER_STATUS_REJECT_WAIT_FINANCE);
				order.setCheckstatus(Constants.HY_ORDER_CHECK_STATUS_REJECT);

				// 开启驳回审核流程
				Map<String, Object> map = new HashMap<>();
				map.put("person", "store");

				ProcessInstance pi = runtimeService.startProcessInstanceByKey("suppilerDismissOrder", map);
				// 根据流程实例id查询任务
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), view + ":1");
				taskService.complete(task.getId());

				SupplierDismissOrderApply supplierDismissOrderApply = new SupplierDismissOrderApply();
				supplierDismissOrderApply.setOrderId(order.getId());
				supplierDismissOrderApply.setCreateTime(new Date());
				supplierDismissOrderApply.setOperator(admin);
				supplierDismissOrderApply.setStatus(0); // 待审核
				supplierDismissOrderApply.setType(0); // 门店驳回申请
				supplierDismissOrderApply.setProcessInstanceId(pi.getProcessInstanceId());
				supplierDismissOrderApply.setMoney(order.getJiusuanMoney()); // 应退给门店的金额
				System.out.println(order.getJiusuanMoney());
				supplierDismissOrderApplyService.save(supplierDismissOrderApply);

				/* add by liyang,change the insurance order status */
				List<Filter> insurancefilters = new ArrayList<>();
				insurancefilters.add(Filter.eq("orderId", id));
				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,insurancefilters,null);
				if(!insuranceOrders.isEmpty()){
					for(InsuranceOrder tmp:insuranceOrders){
						//将保险状态设置为已取消状态
						tmp.setStatus(2);
						insuranceOrderService.update(tmp);
					}
				}

			} else {
				order.setStatus(Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM);
				order.setCheckstatus(Constants.HY_ORDER_CHECK_STATUS_ACCEPT);
			}

			HyOrderApplication application = new HyOrderApplication();
			application.setContent("门店确认订单");
			application.setView(view);
			application.setStatus(status);
			application.setOrderId(id);
			application.setCreatetime(new Date());
			application.setOperator(admin);
			application.setType(HyOrderApplication.STORE_CONFIRM_ORDER);
			hyOrderApplicationService.save(application);

			hyOrderService.update(order);
			json.setSuccess(true);
			json.setMsg("门店确认成功");
			json.setObj(null);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("门店确认失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;

	}

	// 供应商确认订单
	@RequestMapping(value = "provider_confirm")
	@ResponseBody
	public Json providerConfirm(Long id, String view, Integer status, HttpSession session) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Json json = new Json();
		try {

			json = hyOrderService.providerConfirm(id,view,status,session);

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("供应商确认失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;

	}

	/**
     *  在payableLineItem中增加数据
     * */
    private void savePayablesLineItem(PayablesLine payablesLine, HyOrder order, HySupplierContract contract, HyGroup hyGroup, HyLine hyLine, Date date, BigDecimal money) {
        PayablesLineItem item = new PayablesLineItem();
        item.setPayablesLineId(payablesLine.getId());
        item.setHyOrder(order);
        item.setSupplierContract(contract);
        item.setOperator(hyLine.getOperator());
        // 1线路 2酒店 3门票 4酒加景 5签证 6认购门票
        item.setProductType(1);
        item.setHyGroup(hyGroup);
        item.setSn(hyLine.getPn());
        item.setProductName(hyLine.getName());
        item.settDate(hyGroup.getStartDay());
        item.setSettleDate(date);
        item.setOrderMoney(money);
        item.setKoudian(order.getKoudianMoney());
        item.setMoney(money.subtract(order.getKoudianMoney()));
        // 0:未提交 1:已提交
        item.setState(0);
        payablesLineItemService.save(item);
    }

    static class MyOrderItem implements Serializable {
		private Long itemId;
		private String name;
		private Integer type;
		private Integer priceType;
		private Integer number;
		private Integer returnNumber;
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

		public Integer getReturnNumber() {
			return returnNumber;
		}

		public void setReturnNumber(Integer returnNumber) {
			this.returnNumber = returnNumber;
		}

	}

	@RequestMapping(value = "scg_list")
	@ResponseBody
	public Json scgList(Long id) {
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}

			BigDecimal lineRefundPercentage = hyOrderService.getLineRefundPercentage(order).multiply(BigDecimal.valueOf(0.01));
			HyGroup group  = hyGroupService.find(order.getGroupId());
			List<MyOrderItem> lists = new ArrayList<>();
			for (HyOrderItem item : order.getOrderItems()) {
				MyOrderItem myOrderItem = new MyOrderItem();
				myOrderItem.setItemId(item.getId());
				myOrderItem.setType(item.getType());
				myOrderItem.setPriceType(item.getPriceType());
				myOrderItem.setName(item.getName());
				if(!item.getHyOrderCustomers().isEmpty())
					myOrderItem.setCustomerName(item.getHyOrderCustomers().get(0).getName());
				myOrderItem.setNumber(item.getNumber());
				myOrderItem.setReturnNumber(item.getNumberOfReturn());
				if(item.getType()==8){	//线路其他价格
					myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
					myOrderItem.setJiesuanRefund(item.getJiesuanPrice().multiply(
							(order.getJiesuanMoney1().subtract(order.getStoreFanLi()).subtract(order.getDiscountedPrice())).divide(
									order.getJiesuanMoney1().subtract(order.getStoreFanLi()),2, RoundingMode.HALF_UP
							)
					).multiply(lineRefundPercentage));
					myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
					myOrderItem.setWaimaiRefund(item.getWaimaiPrice().multiply(
							(order.getJiesuanMoney1().subtract(order.getStoreFanLi()).subtract(order.getDiscountedPrice())).divide(
									order.getJiesuanMoney1().subtract(order.getStoreFanLi()),2,RoundingMode.HALF_UP
							)
					).multiply(lineRefundPercentage));


				}else if(item.getType()==1){  //线路
					myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
					myOrderItem.setJiesuanRefund(item.getJiesuanPrice().subtract(group.getFanliMoney().multiply(
							BigDecimal.valueOf(item.getHyOrderCustomers().size()))).multiply(
							(order.getJiesuanMoney1().subtract(order.getStoreFanLi().subtract(order.getDiscountedPrice()))).divide(
									order.getJiesuanMoney1().subtract(order.getStoreFanLi()),2,RoundingMode.HALF_UP
							)
					).multiply(lineRefundPercentage));
					myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
					myOrderItem.setWaimaiRefund(item.getWaimaiPrice().subtract(group.getFanliMoney().multiply(
							BigDecimal.valueOf(item.getHyOrderCustomers().size()))).multiply(
							(order.getJiesuanMoney1().subtract(order.getStoreFanLi().subtract(order.getDiscountedPrice()))).divide(
									order.getJiesuanMoney1().subtract(order.getStoreFanLi()),2,RoundingMode.HALF_UP
							)
					).multiply(lineRefundPercentage));

				}

				myOrderItem.setBaoxianJiesuanPrice(hyOrderItemService.getBaoxianJiesuanPrice(item));
				myOrderItem.setBaoxianJiesuanRefund(myOrderItem.getBaoxianJiesuanPrice());
				myOrderItem.setBaoxianWaimaiPrice(hyOrderItemService.getBaoxianWaimaiPrice(item));
				myOrderItem.setBaoxianWaimaiRefund(myOrderItem.getBaoxianWaimaiPrice());
				lists.add(myOrderItem);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(lists);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping(value = "store_cancel_group/list/view")
	@ResponseBody
	public Json storeCancelGroupList(Pageable pageable, Integer status, String providerName, HttpSession session) {
		return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session, HyOrderApplication.STORE_CANCEL_GROUP,1);
	}

	@RequestMapping(value = "store_customer_service/list/view")
	@ResponseBody
	public Json storeCustomerServiceList(Pageable pageable, Integer status, String providerName, HttpSession session) {
		return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session,
				HyOrderApplication.STORE_CUSTOMER_SERVICE,1);
	}



	@RequestMapping(value = { "store_cancel_group/detail/view", "store_customer_service/detail/view" })
	@ResponseBody
	public Json storeCancelGroupDetail(Long id) {
		Json json = new Json();

		try {
			HyOrderApplication application = hyOrderApplicationService.find(id);
			if (application == null) {
				throw new Exception("没有有效的审核申请记录");
			}
			HyOrder order = hyOrderService.find(application.getOrderId());

			Map<String, Object> ans = new HashMap<>();

			/** 审核详情 */
			ans.put("application", hyOrderApplicationService.auditDetailHelper(application, application.getStatus()));
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

	@Transactional
	@RequestMapping(value = "store_cancel_group/apply", method = RequestMethod.POST)
	@ResponseBody
	public Json storeCancelGroupApply(@RequestBody HyOrderApplication application, HttpSession session) {
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
			if(order.getIfjiesuan()==true) {
				json.setSuccess(false);
				json.setMsg("结算后不能退团");
				json.setObj(2);
				return json;
			}
			HyGroup group = hyGroupService.find(order.getGroupId());

			Map<String, Object> variables = new HashMap<>();

			HyAdmin provider = group.getCreator();

			// 指定审核供应商
			variables.put("provider", provider.getUsername());

			// 启动流程
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeTuiTuan", variables);
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();

			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), "发起申请:1");
			taskService.complete(task.getId(), variables);

			application.setContent("门店退团");
			application.setOperator(admin);
			application.setStatus(0); // 待供应商审核
			application.setCreatetime(new Date());
			application.setProcessInstanceId(task.getProcessInstanceId());
			application.setType(HyOrderApplication.STORE_CANCEL_GROUP);
			order.setRefundstatus(1); // 订单退款状态为退款中
			//
			hyOrderService.update(order);

			for (HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
				item.setHyOrderApplication(application);
			}

			hyOrderApplicationService.save(application);
			

			json.setSuccess(true);
			json.setMsg("门店退团申请成功");
			json.setObj(null);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("门店退团申请失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	@Autowired
	BranchBalanceService branchBalanceService;

    @Autowired
	BranchPreSaveService branchPreSaveService;

	@RequestMapping(value = "store_cancel_group/audit", method = RequestMethod.POST)
	@ResponseBody
	public Json storeCancelGroupAudit(Long id, String comment, Integer auditStatus, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyOrderApplication application = hyOrderApplicationService.find(id);
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
					filters.add(Filter.eq("eduleixing", Eduleixing.storeTuiTuanLimit));
					List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
					BigDecimal money = edu.get(0).getMoney();
					BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianWaimaiMoney());
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
					/* add by liyang,change the insurance order status */
					List<Filter> insurancefilters = new ArrayList<>();
					insurancefilters.add(Filter.eq("orderId", id));
					List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,insurancefilters,null);
					if(!insuranceOrders.isEmpty()){
						for(InsuranceOrder tmp:insuranceOrders){
							//设置保险为已取消状态
							tmp.setStatus(2);
							insuranceOrderService.update(tmp);
						}
					}
					// 门店退团退款审核成功，进行订单处理
					hyOrderApplicationService.handleStoreTuiTuan(application);

					
					
					// write by wj
					application.setStatus(4); // 已退款
					
					Long orderId = application.getOrderId();
					HyOrder hyOrder = hyOrderService.find(orderId);
					Long storeId = hyOrder.getStoreId();
					Store store = storeService.find(storeId);
					
					/*add by liyang, sets the fdd_contract state to resign*/
					Long contractId = hyOrder.getContractId();
					if(contractId!=null && hyOrder.getContractNumber()!=null){
						if(hyOrder.getContractType()==0){
							FddDayTripContract curr = fddDayTripContractService.find(contractId);
							if(hyOrder.getPeople()==0){
								//如果所有人都退完了，设置合同为取消
								curr.setCancelDate(new Date());
								curr.setStatus(5);
							}else{
								//如果是部分退团，则需重新签署合同
								curr.setStatus(6);
							}
						}else{
							FddContract curr = fddContractService.find(contractId);
							if(hyOrder.getPeople()==0){
								//如果所有人都退完了，设置合同为取消
								curr.setCancelDate(new Date());
								curr.setStatus(5);
							}else{
								//如果是部分退团，则需重新签署合同
								curr.setStatus(6);
							}
						}
					}
					
					//写入退款记录   //预存款余额修改
					BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianWaimaiMoney());
					RefundInfo refundInfo = new RefundInfo();
					refundInfo.setAmount(tuiKuan);
					refundInfo.setAppliName(application.getOperator().getName());
					refundInfo.setApplyDate(application.getCreatetime());
					Date date = new Date();
					refundInfo.setPayDate(date);
					refundInfo.setRemark("门店退团退款");
					refundInfo.setState(1);  //已付款
					refundInfo.setType(1);  //门店退团（游客退团，认为是一个类型）
					refundInfo.setOrderId(orderId);
					refundInfoService.save(refundInfo);
					
					
					//生成退款记录
					RefundRecords records = new RefundRecords();
					records.setRefundInfoId(refundInfo.getId());
					records.setOrderCode(hyOrder.getOrderNumber());
					records.setOrderId(hyOrder.getId());
					records.setRefundMethod((long) 1); //预存款方式
					records.setPayDate(date);
					HyAdmin hyAdmin = hyAdminService.find(username);
					if(hyAdmin!=null)
						records.setPayer(hyAdmin.getName());
					records.setAmount(tuiKuan);
					records.setStoreId(storeId);
					records.setStoreName(store.getStoreName());
					records.setTouristName(hyOrder.getContact());
					records.setTouristAccount(store.getBankList().getBankAccount());  //门店账号
					records.setSignUpMethod(1);   //门店
					refundRecordsService.save(records);

					PayandrefundRecord record = new PayandrefundRecord();
					record.setOrderId(orderId);
					record.setMoney(tuiKuan);
					record.setPayMethod(5);	//5预存款
					record.setType(1);	//1退款
					record.setStatus(1);	//1已退款
					record.setCreatetime(date);
					payandrefundRecordService.save(record);


					if(store.getStoreType()==2){
						HyCompany company = store.getSuoshuDepartment().getHyCompany();
						//修改分公司余额
						List<Filter> branchBalanceFilters = new ArrayList<>();
						branchBalanceFilters.add(Filter.eq("branchId",store.getDepartment().getId()));

						List<BranchBalance> branchBalances = branchBalanceService.findList(null,branchBalanceFilters,null);
						if(branchBalances.size()!=0){
							BranchBalance branchBalance = branchBalances.get(0);
							branchBalance.setBranchBalance(branchBalance.getBranchBalance().add(tuiKuan));
							branchBalanceService.update(branchBalance);
						}else{
							BranchBalance branchBalance = new BranchBalance();
							branchBalance.setBranchId(store.getDepartment().getId());
							branchBalance.setBranchBalance(tuiKuan);
							branchBalanceService.save(branchBalance);
						}
						//分公司预存款记录
						BranchPreSave branchPreSave = new BranchPreSave();
						branchPreSave.setBranchName(company.getCompanyName());
						branchPreSave.setPreSaveBalance(branchBalances.get(0).getBranchBalance());
						branchPreSave.setAmount(tuiKuan);
						branchPreSave.setBranchId(store.getDepartment().getId());
						branchPreSave.setDate(new Date());
						branchPreSave.setDepartmentName(store.getDepartment().getName());
						branchPreSave.setOrderId(hyOrder.getId());
						branchPreSave.setRemark("门店退团");
						branchPreSave.setType(10); //退团
						branchPreSaveService.save(branchPreSave);

					}else{
						//预存款余额表
						// 3、修改门店预存款表      并发情况下的数据一致性！

						List<Filter> filters = new ArrayList<>();
						filters.add(Filter.eq("store", store));
						List<StoreAccount> list = storeAccountService.findList(null, filters, null);
						if(list.size()!=0){
							StoreAccount storeAccount = list.get(0);
							storeAccount.setBalance(storeAccount.getBalance().add(tuiKuan));
							storeAccountService.update(storeAccount);
						}else{
							StoreAccount storeAccount = new StoreAccount();
							storeAccount.setStore(store);
							storeAccount.setBalance(tuiKuan);
							storeAccountService.save(storeAccount);
						}

						// 4、修改门店预存款记录表
						StoreAccountLog storeAccountLog = new StoreAccountLog();
						storeAccountLog.setStatus(1);
						storeAccountLog.setCreateDate(application.getCreatetime());
						storeAccountLog.setMoney(tuiKuan);
						storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
						storeAccountLog.setStore(store);
						storeAccountLog.setType(3);
						storeAccountLog.setProfile("门店退团");
						storeAccountLogService.update(storeAccountLog);

						// 5、修改 总公司-财务中心-门店预存款表
						StorePreSave storePreSave = new StorePreSave();
						storePreSave.setStoreId(storeId);
						storePreSave.setStoreName(store.getStoreName());
						storePreSave.setType(2); ////1:门店充值 2:报名退款  3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款 10:酒店/酒加景销售 11:酒店/酒加景退款 12:门店后返 13:供应商驳回订单 14:
						storePreSave.setDate(date);
						storePreSave.setAmount(tuiKuan);
						storePreSave.setOrderCode(hyOrder.getOrderNumber());
						storePreSave.setOrderId(orderId);
						storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters, null).get(0).getBalance());
						storePreSaveService.save(storePreSave);

					}

					/*
					 * write by lbc
					 * 减少团成员的功能有问题，重写
					 */
					
					
					//拿到groupID备用
					HyGroup group = hyGroupService.find(hyOrder.getGroupId());
					//郭哥用
					Long groupID = group.getId();
					
					//想拿到orderCustomer
					
					//通过HyOrderApplication找到它的item表
					List<HyOrderCustomer> hyOrderCustomers = new ArrayList<>();
					List<HyOrderApplicationItem> hyOrderApplicationItems = application.getHyOrderApplicationItems();
					//找到退货数量大于1的id
					for(HyOrderApplicationItem hyOrderApplicationItem : hyOrderApplicationItems) {
						if(hyOrderApplicationItem.getReturnQuantity() > 0) {
							//退货入量大于1同时类型是线路 而不是其他价格
							HyOrderItem hyOrderItem = hyOrderItemService.find(hyOrderApplicationItem.getItemId());
							if(hyOrderItem.getType() == 1) {
								hyOrderCustomers.addAll(hyOrderItem.getHyOrderCustomers());
							}
						}
					}
					
					//删除member和修改devide表格里面的数据
					if(hyOrderCustomers.size() > 0) {
						//对每一个orderCustomer
						for(HyOrderCustomer hyOrderCustomer : hyOrderCustomers) {
							List<Filter> groupMemberFilter = new ArrayList<>();
							groupMemberFilter.add(Filter.eq("hyGroup", group));
							//这里需要循环做 不能直接写HyOrderCustomerAll 因为每次的customer都不一样
							groupMemberFilter.add(Filter.eq("hyOrderCustomer", hyOrderCustomer));
							List<GroupMember> groupMembers = groupMemberService.findList(null, groupMemberFilter, null);
							for(GroupMember groupMember : groupMembers) {
								String subGroupsn = groupMember.getSubGroupsn();
								List<Filter> groupDivideFilter = new ArrayList<>();
								groupDivideFilter.add(Filter.eq("group", group));
								groupDivideFilter.add(Filter.eq("subGroupsn", subGroupsn));
								List<GroupDivide> groupDivides = groupDivideService.findList(null, groupDivideFilter, null);
								//非空
								if(!groupDivides.isEmpty()){
									//遍历把 数量减一 更新回去
									for(GroupDivide groupDivideItem: groupDivides){
										Integer tempNumber = groupDivideItem.getSubGroupNo();
										tempNumber--;
										groupDivideItem.setSubGroupNo(tempNumber);
										//更新回去
										groupDivideService.update(groupDivideItem);
									}	
								}
								groupMemberService.delete(groupMember);
							}
							
						}
					}

					/*
					 * write by lbc
					 * change by cwz
					 */
					
					// 向groupmember表中减少1条数据
					// 分团中相应团的subGroupNo-n
					// 根据订单找到相应的order_item_id
					// 再根据order_item_id找到order_customer_id
					// 每个ordercustomer_id n + 1

					
					//change by cwz		
//					//————————————add by cwz————————————
//					//一个纯后台的接口 
//					
//					//第一步 找到很多的 HyOrderApplication，放在List里面
////					List<Filter> hyOrderApplicationServiceFilter = new ArrayList<>();
////					hyOrderApplicationServiceFilter.add(Filter.ge("status", 3));
////					hyOrderApplicationServiceFilter.add(Filter.eq("type", 0));
////					List<HyOrderApplication> hyOrderApplications = hyOrderApplicationService.findList(null, hyOrderApplicationServiceFilter, null);
//					
//					//先测试，再看什么时候写这个容错
//					
//					//第二步 拿到groupID备用
//					Long groupID = null;
//					HyGroup group = null;
//					Long order_id;
//					//每次出来只有一个值 按照逻辑来的
//					
//					order_id = application.getOrderId();
//					HyOrder order = hyOrderService.find(order_id);
//					group = hyGroupService.find(order.getGroupId());
//					groupID = group.getId();
//					
//					//第三步 想拿到orderCustom这个名字
//					
//					//第一小步 通过HyOrderApplication找到它的item表
//					//数据库中发现，item根本没有外键，这里不做修改（不用外键也行）
//					List<HyOrderCustomer> HyOrderCustomerAll = new ArrayList<>();
//					HyOrderApplication myHyOrderApplication = null;
//					//得到唯一的myHyOrderApplication
//					myHyOrderApplication = application;
//					
//					//第二小步 筛选相应ID，结算>0的那些Item 不确定对不对
//					//一个orderApplication查出下面多个Item
//					List<HyOrderApplicationItem> hyOrderApplicationItems = new ArrayList<>();
//					//去筛选退款金额大于0的
//					List<HyOrderApplicationItem> hyOrderApplicationItemsPre = new ArrayList<>();
//					
//					hyOrderApplicationItemsPre = myHyOrderApplication.getHyOrderApplicationItems();
//
//					for(HyOrderApplicationItem items: hyOrderApplicationItemsPre){
//						BigDecimal zero = new BigDecimal("0");
//						int flag = items.getJiesuanRefund().compareTo(zero);
//						if(flag == 1){//等于1是 结果大于0
//							hyOrderApplicationItems.add(items);
//						}
//					}
//					
//					
//					//第三小步 根据HyOrderApplicationItem找到其中的order_customer
//					//需要根据orderItem的ID去查orderCustomer 一个Item查出多个orderCustomer
//					if(!hyOrderApplicationItems.isEmpty()){
//						List<Filter> hyOrderCustomerFilter = new ArrayList<>();
//						for(HyOrderApplicationItem myItems: hyOrderApplicationItems){
//							//从HyOrderApplicationItem得到orderItem 
//							//TODO 顿一下
//							HyOrderItem hyOrderItem = hyOrderItemService.find(myItems.getItemId());
//							hyOrderCustomerFilter.add(Filter.eq("orderItem", hyOrderItem));
//							List<HyOrderCustomer> HyOrderCustomers = new ArrayList<>();
//							HyOrderCustomers = hyOrderCustomerService.findList(null, hyOrderCustomerFilter, null);
//							//每次找到的所有customer类 都加到大数组里面
//							for(HyOrderCustomer hyOrderCustomer: HyOrderCustomers){
//								HyOrderCustomerAll.add(hyOrderCustomer);
//							}
//							hyOrderCustomerFilter.clear();
//							
//						}
//					}
//					
//					//第四步 删除member和修改devide表格里面的数据
//					//没有元素是true 有元素是false 想让他有元素是true 进循环 加“!”
//					List<String> subGroupNumbers = new ArrayList<>();
//					if(groupID != null && !HyOrderCustomerAll.isEmpty()){
//					
//						//第一小步 在group_menber表格里面 用容错项找ABCD什么的 把这一条数据删除
//						for(HyOrderCustomer hyOrderCustomerItem: HyOrderCustomerAll){
//							List<Filter> groupMemberFilter = new ArrayList<>();
//							groupMemberFilter.add(Filter.eq("hyGroup", group));
//							//这里需要循环做 不能直接写HyOrderCustomerAll 因为每次的customer都不一样
//							groupMemberFilter.add(Filter.eq("hyOrderCustomer", hyOrderCustomerItem));
//							List<GroupMember> ourGroupMember = groupMemberService.findList(null, groupMemberFilter, null);
//							//删除之前先确定有值
//							if(!ourGroupMember.isEmpty()){
//								//遍历得出所有的 Item，进而得到ABCD
//								for(GroupMember groupMemberItem: ourGroupMember){
//									subGroupNumbers.add(groupMemberItem.getSubGroupsn());
//								}
//								//每次找到的member都不一样 每次删除每次的
//								for(GroupMember groupMemberItem: ourGroupMember){
//									groupMemberService.delete(groupMemberItem);
//								}
//							
//							}
//						}
//						
//						//第二小步 在group_devide表格里面 用ABCD和group_id去找sub_group_no数量减一
//						//遍历刚刚的ABCD数组 如果不是空值
//						if(!subGroupNumbers.isEmpty()){
//							for(String groupSN: subGroupNumbers){
//								List<Filter> groupDevideFilter = new ArrayList<>();
//								groupDevideFilter.add(Filter.eq("group", group));
//								groupDevideFilter.add(Filter.eq("subGroupsn", groupSN));
//								List<GroupDivide> tempGroupDivide = groupDivideService.findList(null, groupDevideFilter, null);
//								if(!tempGroupDivide.isEmpty()){
//									//遍历把 数量减一 更新回去
//									for(GroupDivide groupDivideItem: tempGroupDivide){
//										Integer tempNumber = groupDivideItem.getSubGroupNo();
//										tempNumber--;
//										groupDivideItem.setSubGroupNo(tempNumber);
//										//更新回去
//										groupDivideService.update(groupDivideItem);
//									}	
//								}
//							}
//						}
//					}
					
					/**added by GSbing,20190301,门店退团后修改计调报账中游客数量*/
					List<Filter> hyRegulateFilter = new ArrayList<>();
					hyRegulateFilter.add(Filter.eq("hyGroup", groupID));
					List<HyRegulate> hyRegulates = hyRegulateService.findList(null, hyRegulateFilter, null);
					if(hyRegulates.size() != 0) {
						HyRegulate hyRegulate = hyRegulates.get(0);
						//计算退团数量
						List<HyOrderApplicationItem> applicationItems=application.getHyOrderApplicationItems();
						Integer returnNum=0;
						for(HyOrderApplicationItem item:applicationItems) {
							returnNum=returnNum+item.getReturnQuantity();
						}
						hyRegulate.setVisitorNum(hyRegulate.getVisitorNum()-returnNum);
						hyRegulateService.update(hyRegulate);
					}
					
					
					//————————————end add————————————
					
				}

			} else {
				map.put("msg", "false");
				application.setStatus(5); // 已驳回
				HyOrder order = hyOrderService.find(application.getOrderId());
				order.setRefundstatus(4); // 退款已驳回
				hyOrderService.update(order);

			}
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), processInstanceId,
					(comment == null ? "审核通过" : comment) + ":" + auditStatus);
			taskService.complete(task.getId(), map);
			hyOrderApplicationService.update(application);
			json.setSuccess(true);
			json.setMsg("审核成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping(value = "scs_list")
	@ResponseBody
	public Json scsList(Long id) {
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}
			HyGroup group  = hyGroupService.find(order.getGroupId());
			BigDecimal lineRefundPercentage = hyOrderService.getLineRefundPercentage(order).multiply(BigDecimal.valueOf(0.01));
			List<MyOrderItem> lists = new ArrayList<>();
			for (HyOrderItem item : order.getOrderItems()) {
				MyOrderItem myOrderItem = new MyOrderItem();
				myOrderItem.setItemId(item.getId());
				myOrderItem.setType(item.getType());
				myOrderItem.setPriceType(item.getPriceType());
				myOrderItem.setName(item.getName());
				myOrderItem.setNumber(item.getNumber());
				myOrderItem.setReturnNumber(item.getNumberOfReturn());
				if(item.getType()==8){	//线路其他价格
					myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
					myOrderItem.setJiesuanRefund(item.getJiesuanPrice().multiply(
							(order.getJiesuanMoney1().subtract(order.getStoreFanLi()).subtract(order.getDiscountedPrice())).divide(
									order.getJiesuanMoney1().subtract(order.getStoreFanLi()),2,RoundingMode.HALF_UP
							)
					).multiply(lineRefundPercentage));
					myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
					myOrderItem.setWaimaiRefund(item.getWaimaiPrice().multiply(
							(order.getJiesuanMoney1().subtract(order.getStoreFanLi()).subtract(order.getDiscountedPrice())).divide(
									order.getJiesuanMoney1().subtract(order.getStoreFanLi()),2,RoundingMode.HALF_UP
							)
					).multiply(lineRefundPercentage));


				}else if(item.getType()==1){  //线路
					myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
					myOrderItem.setJiesuanRefund(item.getJiesuanPrice().subtract(group.getFanliMoney().multiply(
							BigDecimal.valueOf(item.getHyOrderCustomers().size()))).multiply(
							(order.getJiesuanMoney1().subtract(order.getStoreFanLi().subtract(order.getDiscountedPrice()))).divide(
									order.getJiesuanMoney1().subtract(order.getStoreFanLi()),2,RoundingMode.HALF_UP
							)
					).multiply(lineRefundPercentage));
					myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
					myOrderItem.setWaimaiRefund(item.getWaimaiPrice().subtract(group.getFanliMoney().multiply(
							BigDecimal.valueOf(item.getHyOrderCustomers().size()))).multiply(
							(order.getJiesuanMoney1().subtract(order.getStoreFanLi().subtract(order.getDiscountedPrice()))).divide(
									order.getJiesuanMoney1().subtract(order.getStoreFanLi()),2,RoundingMode.HALF_UP
							)
					).multiply(lineRefundPercentage));

				}

				myOrderItem.setBaoxianJiesuanPrice(hyOrderItemService.getBaoxianJiesuanPrice(item));
				myOrderItem.setBaoxianJiesuanRefund(myOrderItem.getBaoxianJiesuanPrice());
				myOrderItem.setBaoxianWaimaiPrice(hyOrderItemService.getBaoxianWaimaiPrice(item));
				myOrderItem.setBaoxianWaimaiRefund(myOrderItem.getBaoxianWaimaiPrice());


				if(!item.getHyOrderCustomers().isEmpty()){
					myOrderItem.setCustomerName(item.getHyOrderCustomers().get(0).getName());
				}
					


				//myOrderItem.setCustomerName(item.getHyOrderCustomers().get(0).getName());


				lists.add(myOrderItem);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(lists);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping(value = "store_customer_service/apply", method = RequestMethod.POST)
	@ResponseBody
	public Json storeCustomerServiceApply(@RequestBody HyOrderApplication application, HttpSession session) {
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
//			if(order.getFatuandate().compareTo(new Date())>0) {
//				json.setSuccess(false);
//				json.setMsg("发团前不能申请售后退款");
//				json.setObj(2);
//				return json;
//			}
			HyGroup group = hyGroupService.find(order.getGroupId());
			HyAdmin provider = group.getCreator();

			Map<String, Object> variables = new HashMap<>();

			// 制定审核供应商
			variables.put("provider", provider.getUsername());

			// 启动流程
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeShouHou", variables);
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();

			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), "发起申请:1");
			taskService.complete(task.getId(), variables);

			application.setContent("门店售后");
			application.setOperator(admin);
			application.setStatus(0); // 待供应商审核
			application.setCreatetime(new Date());
			application.setProcessInstanceId(task.getProcessInstanceId());
			application.setType(HyOrderApplication.STORE_CUSTOMER_SERVICE);
			order.setRefundstatus(1); // 订单退款状态为退款中

			hyOrderService.update(order);

			for (HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
				item.setHyOrderApplication(application);
			}

			hyOrderApplicationService.save(application);

			json.setSuccess(true);
			json.setMsg("门店售后申请成功");
			json.setObj(null);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("门店售后申请失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
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
//			String applyName = application.getOperator().getUsername(); // 找到提交申请的人
			String processInstanceId = application.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId.equals("")) {
				throw new Exception("审核出错，信息不完整，请重新申请");
			}

			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			HashMap<String, Object> map = new HashMap<>();

			if (auditStatus.equals(1)) { // 如果审核通过
				map.put("msg", "true");
				if (task.getTaskDefinitionKey().equals("usertask2")) {
					// 如果是供应商
					// 设置下一阶段审核的部门
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("eduleixing", Eduleixing.storeShouHouLimit));
					List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
					BigDecimal money = edu.get(0).getMoney();
					BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianWaimaiMoney());
					if (tuiKuan.compareTo(money) > 0) { // 如果退款总额大于限额
						map.put("money", "more"); // 设置需要品控中心限额审核
						application.setStatus(1); // 待品控审核
					} else { // 如果退款总额不大于限额
						map.put("money", "less"); // 设置财务审核
						application.setStatus(2); // 待财务审核
					}
				} else if (task.getTaskDefinitionKey().equals("usertask3")) { // 如果品控
					application.setStatus(2); // 待财务审核
				} else if (task.getTaskDefinitionKey().equals("usertask4")) { // 如果财务

					// 门店售后退款成功，进行订单处理
					hyOrderApplicationService.handleStoreShouHou(application);

					//add by wj
					HyOrder hyOrder = hyOrderService.find(application.getOrderId());
					HyOrderItem hyOrderItem = hyOrder.getOrderItems().get(0);
					Long groupId = hyOrderItem.getProductId(); // prodectId实际上为groupId
					HyGroup hyGroup = hyGroupService.find(groupId);
					
					HyLine hyLine = hyGroup.getLine();
					HySupplierContract contract = hyLine.getContract(); // 获取合同
//					Long supplierId = hyLine.getHySupplier().getId();
//					Long supplierContractId = hyLine.getContract().getId();

//					Settle settle = contract.getSettle(); // 获取结算方式
//					if(Settle.shishijie.equals(settle)){   //可以改为已结算	
						BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianWaimaiMoney());
						BigDecimal balance = new BigDecimal(0);
						List<Filter> filters = new ArrayList<>();
						filters.add(Filter.eq("supplierName",contract.getLiable().getUsername()));
						List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null,filters,null);
						
						//更新欠款
						if(receiptTotalServicers.size()!=0){
							ReceiptTotalServicer receiptTotalServicer = receiptTotalServicers.get(0);
							balance = receiptTotalServicer.getBalance();
							balance = balance.add(tuiKuan);
							receiptTotalServicer.setBalance(balance);
							receiptTotalServicerService.update(receiptTotalServicer);				
						}else{
							ReceiptTotalServicer receiptTotalServicer = new ReceiptTotalServicer();
							receiptTotalServicer.setSupplierName(contract.getLiable().getUsername());
							balance = balance.add(tuiKuan);
							receiptTotalServicer.setBalance(tuiKuan);
							receiptTotalServicerService.save(receiptTotalServicer);
						}
						
						//写入售后退消团收支明细  -- 收
						ReceiptServicer receiptServicer = new ReceiptServicer();
						receiptServicer.setAmount(tuiKuan);
						receiptServicer.setDate(new Date());
						receiptServicer.setOrderOrPayServicerId(hyOrder.getId());
						receiptServicer.setOperator(hyAdminService.find(username).getName());
						receiptServicer.setSupplierName(contract.getLiable().getUsername());
						receiptServicer.setState(0);  //存入欠款
						receiptServicer.setBalance(balance);
						receiptServicerService.save(receiptServicer);
	
						//更新预存款相关部分
						Long storeId = hyOrder.getStoreId();
						Store store = storeService.find(storeId);
						
						//add by wj  更新扣点
						BigDecimal koudian = new BigDecimal(0);
//						if(hyOrder.getKoudianMethod().equals(Constants.DeductLine.rentou)){
//							BigDecimal renshu = new BigDecimal("0.0");
//							for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
//								HyOrderItem hyOrderItem2 = hyOrderItemService.find(item.getItemId());
//								for(HyOrderCustomer hyOrderCustomer:hyOrderItem2.getHyOrderCustomers()){
//									renshu = renshu.add(new BigDecimal(1));
//								}
//							}
//							koudian = renshu.multiply(hyOrder.getHeadProportion());
//						}else{
//							koudian = tuiKuan.multiply(hyOrder.getProportion()).multiply(BigDecimal.valueOf(0.01));
//						}	
						if(hyOrder.getKoudianMethod().equals(Constants.DeductLine.tuanke.ordinal())){
							koudian = application.getJiesuanMoney().multiply(hyOrder.getProportion()).multiply(BigDecimal.valueOf(0.01));
						}
						boolean ifSameMonth = isSameMonth(receiptServicer.getDate(),hyOrder.getJiesuantime());	
						
						//写入退款记录   //预存款余额修改
						RefundInfo refundInfo = new RefundInfo();
						refundInfo.setAmount(tuiKuan);
						refundInfo.setAppliName(application.getOperator().getName());
						refundInfo.setApplyDate(application.getCreatetime());
						Date date = new Date();
						refundInfo.setPayDate(date);
						refundInfo.setRemark("门店售后退款");
						refundInfo.setState(1);  //已付款
						refundInfo.setType(13);  //门店售后退款
						refundInfo.setKoudian(koudian);
						refundInfo.setOrderId(hyOrder.getId());
						if(ifSameMonth){
							BigDecimal orderkoudian = hyOrder.getKoudianMoney();
							orderkoudian = orderkoudian.subtract(koudian);
							hyOrder.setKoudianMoney(orderkoudian);
							hyOrderService.update(hyOrder);
							refundInfo.setIfTongji(true);
						}else{
							refundInfo.setIfTongji(false);
						}
						
						refundInfoService.save(refundInfo);
						
						
						//生成退款记录
						RefundRecords records = new RefundRecords();
						records.setRefundInfoId(refundInfo.getId());
						records.setOrderCode(hyOrder.getOrderNumber());
						records.setOrderId(hyOrder.getId());
						records.setRefundMethod((long) 1); //预存款方式
						records.setPayDate(date);
						HyAdmin hyAdmin = hyAdminService.find(username);
						if(hyAdmin!=null)
							records.setPayer(hyAdmin.getName());
						records.setAmount(tuiKuan);
						records.setStoreId(storeId);
						records.setStoreName(store.getStoreName());
						records.setTouristName(hyOrder.getContact());
						records.setTouristAccount(store.getBankList().getBankAccount());  //门店账号
						records.setSignUpMethod(1);   //门店
						refundRecordsService.save(records);

						PayandrefundRecord record = new PayandrefundRecord();
						record.setOrderId(hyOrder.getId());
						record.setMoney(tuiKuan);
						record.setPayMethod(5);	//5预存款
						record.setType(1);	//1退款
						record.setStatus(1);	//1已退款
						record.setCreatetime(date);
						payandrefundRecordService.save(record);



					if(store.getStoreType()==2){
						HyCompany company = store.getSuoshuDepartment().getHyCompany();
						//修改分公司余额
						List<Filter> branchBalanceFilters = new ArrayList<>();
						branchBalanceFilters.add(Filter.eq("branchId",store.getDepartment().getId()));

						List<BranchBalance> branchBalances = branchBalanceService.findList(null,branchBalanceFilters,null);
						if(branchBalances.size()!=0){
							BranchBalance branchBalance = branchBalances.get(0);
							branchBalance.setBranchBalance(branchBalance.getBranchBalance().add(tuiKuan));
							branchBalanceService.update(branchBalance);
						}else{
							BranchBalance branchBalance = new BranchBalance();
							branchBalance.setBranchId(store.getDepartment().getId());
							branchBalance.setBranchBalance(tuiKuan);
							branchBalanceService.save(branchBalance);
						}
						//分公司预存款记录
						BranchPreSave branchPreSave = new BranchPreSave();
						branchPreSave.setBranchName(company.getCompanyName());
						branchPreSave.setPreSaveBalance(branchBalances.get(0).getBranchBalance());
						branchPreSave.setAmount(tuiKuan);
						branchPreSave.setBranchId(store.getDepartment().getId());
						branchPreSave.setDate(new Date());
						branchPreSave.setDepartmentName(store.getDepartment().getName());
						branchPreSave.setOrderId(hyOrder.getId());
						branchPreSave.setRemark("门店售后");
						branchPreSave.setType(11); //退团
						branchPreSaveService.save(branchPreSave);

					}else{
						//预存款余额表
						// 3、修改门店预存款表      并发情况下的数据一致性！

						List<Filter> filters2 = new ArrayList<>();
						filters.add(Filter.eq("store", store));
						List<StoreAccount> list = storeAccountService.findList(null, filters2, null);
						if(list.size()!=0){
							StoreAccount storeAccount = list.get(0);
							storeAccount.setBalance(storeAccount.getBalance().add(tuiKuan));
							storeAccountService.update(storeAccount);
						}else{
							StoreAccount storeAccount = new StoreAccount();
							storeAccount.setStore(store);
							storeAccount.setBalance(tuiKuan);
							storeAccountService.save(storeAccount);
						}

						// 4、修改门店预存款记录表
						StoreAccountLog storeAccountLog = new StoreAccountLog();
						storeAccountLog.setStatus(1);
						storeAccountLog.setCreateDate(application.getCreatetime());
						storeAccountLog.setMoney(tuiKuan);
						storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
						storeAccountLog.setStore(store);
						storeAccountLog.setType(3);
						storeAccountLog.setProfile("门店售后");
						storeAccountLogService.update(storeAccountLog);

						// 5、修改 总公司-财务中心-门店预存款表
						StorePreSave storePreSave = new StorePreSave();
						storePreSave.setStoreName(store.getStoreName());
						storePreSave.setType(2); ////1:门店充值 2:报名退款  3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款 10:酒店/酒加景销售 11:酒店/酒加景退款 12:门店后返 13:供应商驳回订单 14:
						storePreSave.setDate(date);
						storePreSave.setAmount(tuiKuan);
						storePreSave.setOrderCode(hyOrder.getOrderNumber());
						storePreSave.setOrderId(hyOrder.getId());
						storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters2, null).get(0).getBalance());
						storePreSaveService.save(storePreSave);

					}

							
//					}
					application.setStatus(4); // 已退款
				}
			} else {
				map.put("msg", "false");
				application.setStatus(5); // 已驳回
				HyOrder order = hyOrderService.find(application.getOrderId());
				order.setRefundstatus(4); // 退款已驳回
				hyOrderService.update(order);
			}
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), processInstanceId,
					(comment == null ? "审核通过" : comment) + ":" + auditStatus);
			taskService.complete(task.getId(), map);
			hyOrderApplicationService.update(application);
			json.setSuccess(true);
			json.setMsg("审核成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("审核失败");
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
			order.setJiesuanMoney1(order.getJiesuanMoney1().subtract(oldAjustMoney).add(adjustMoney));
			order.setAdjustMoney(adjustMoney);


			
			//修改扣点金额
			if(order.getIfjiesuan()==false){	//如果没有结算
				if(order.getKoudianMethod().equals(Constants.DeductLine.tuanke.ordinal())){


					BigDecimal orderMoney = order.getJiusuanMoney().add(order.getTip())
						.subtract(order.getDiscountedPrice()).subtract(order.getStoreFanLi());

					order.setKoudianMoney(order.getProportion().divide(new BigDecimal(100)).multiply(orderMoney));
				}
			}
			
			
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

	@RequestMapping(value = "pay")
	@ResponseBody
	public Json pay(Long id, HttpSession session) {
		Json json = new Json();
		try {
			json = hyOrderService.addStoreOrderPayment(id, session);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("支付错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;

	}
	
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
	
	public boolean isSameMonth(Date date1,Date date2){
		try {
               Calendar cal1 = Calendar.getInstance();
               cal1.setTime(date1);

               Calendar cal2 = Calendar.getInstance();
               cal2.setTime(date2);

               boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                       .get(Calendar.YEAR);
               boolean isSameMonth = isSameYear
                       && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
               return isSameMonth;
        }catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
        return false;
	}
	
}
