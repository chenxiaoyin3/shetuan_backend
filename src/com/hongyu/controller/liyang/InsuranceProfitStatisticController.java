package com.hongyu.controller.liyang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.controller.BaseController;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.controller.liyang.InsuranceStatisticsController.Monthly;
import com.hongyu.controller.liyang.InsuranceStatisticsController.Profit;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.Store;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.StoreService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.DateUtil;
/**
 * 保险利润明细表
 * @author liyang
 *
 */
@Controller
@RequestMapping("/admin/insuranceProfitStatistics/")
public class InsuranceProfitStatisticController {
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "insuranceServiceImpl")
	InsuranceService insuranceService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	BaseController baseController = new BaseController();
	
	public static class Profit{
		private String contact;
		private Integer customerNums;
		private String orderNumber;
		private String linePn;
		private Date groupStartDate;
		private Integer groupDays;
		private String store;
		private String hyOperator;
		private String insuranceName;
		private String type;
		private Date insuredTime;
		private BigDecimal receivedMoney = new BigDecimal(0);
		private BigDecimal payMoney = new BigDecimal(0);
		private BigDecimal profit = new BigDecimal(0);
		public String getContact() {
			return contact;
		}
		public void setContact(String contact) {
			this.contact = contact;
		}
		public Integer getCustomerNums() {
			return customerNums;
		}
		public void setCustomerNums(Integer customerNums) {
			this.customerNums = customerNums;
		}
		public String getOrderNumber() {
			return orderNumber;
		}
		public void setOrderNumber(String orderNumber) {
			this.orderNumber = orderNumber;
		}
		public String getLinePn() {
			return linePn;
		}
		public void setLinePn(String linePn) {
			this.linePn = linePn;
		}
		public Date getGroupStartDate() {
			return groupStartDate;
		}
		public void setGroupStartDate(Date groupStartDate) {
			this.groupStartDate = groupStartDate;
		}
		public Integer getGroupDays() {
			return groupDays;
		}
		public void setGroupDays(Integer groupDays) {
			this.groupDays = groupDays;
		}
		public String getStore() {
			return store;
		}
		public void setStore(String store) {
			this.store = store;
		}
		public String getHyOperator() {
			return hyOperator;
		}
		public void setHyOperator(String hyOperator) {
			this.hyOperator = hyOperator;
		}
		public String getInsuranceName() {
			return insuranceName;
		}
		public void setInsuranceName(String insuranceName) {
			this.insuranceName = insuranceName;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Date getInsuredTime() {
			return insuredTime;
		}
		public void setInsuredTime(Date insuredTime) {
			this.insuredTime = insuredTime;
		}
		public BigDecimal getReceivedMoney() {
			return receivedMoney;
		}
		public void setReceivedMoney(BigDecimal receivedMoney) {
			this.receivedMoney = receivedMoney;
		}
		public BigDecimal getPayMoney() {
			return payMoney;
		}
		public void setPayMoney(BigDecimal payMoney) {
			this.payMoney = payMoney;
		}
		public BigDecimal getProfit() {
			return profit;
		}
		public void setProfit(BigDecimal profit) {
			this.profit = profit;
		}
		
	}
	@RequestMapping("profit/view")
	@ResponseBody
	public Json insuranceProfit(@DateTimeFormat(pattern="yyyy-MM-dd") Date insuredStartTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date insuredEndTime,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date effectStartTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date effectEndTime,
			Integer type,Long storeId,String operator,String customerName,HttpSession session,HttpServletRequest request){
		Json json = new Json();
		try {
			//获取用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			//用户权限
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			
			List<Filter> filters = new ArrayList<>();
			if(insuredStartTime!=null){
				filters.add(Filter.ge("insuredTime", insuredStartTime));
			}
			if(insuredEndTime!=null){
				Date endDate = DateUtil.getEndOfDay(insuredEndTime);
				filters.add(Filter.le("insuredTime", endDate));
//				filters.add(Filter.le("insuredTime", insuredEndTime));
			}
			if(effectStartTime!=null){
				filters.add(Filter.ge("insuranceStarttime", effectStartTime));
			}
			if(effectEndTime!=null){
				Date endEffectDate = DateUtil.getEndOfDay(effectEndTime);
				filters.add(Filter.le("insuranceStarttime", endEffectDate));
//				filters.add(Filter.le("insuranceStarttime", effectEndTime));
			}
			if(type!=null){
				filters.add(Filter.eq("type", type));
			}
			//只统计已投保的的保单
			filters.add(Filter.eq("status", 3));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.asc("createDate"));
			List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,filters,orders);
			HashMap<String, Object> ans = new HashMap<>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			BigDecimal receivedSum = new BigDecimal(0);
			BigDecimal paySum = new BigDecimal(0);
			BigDecimal profitSum = new BigDecimal(0);
			for(InsuranceOrder insuranceOrder:insuranceOrders){
				HyOrder hyOrder = hyOrderService.find(insuranceOrder.getOrderId());
				//先判断当前操作人权限四种权限，
				if(admin.getRole().getName().contains("门店员工") && !operator.equals(admin.getName())){
					continue;
				}
				if(admin.getRole().getName().contains("门店经理")){
					if(admin.getDepartment().getStore().getId().longValue()!=hyOrder.getStoreId().longValue())
						continue;
				}
				if(storeId!=null){		
					if(storeId.longValue()!=hyOrder.getStoreId().longValue()){
						continue;
					}
				}
				if(operator!=null && !hyOrder.getOperator().getName().contains(operator)){
					continue;
				}
				if(customerName!=null && !hyOrder.getContact().contains(customerName)){
					continue;
				}
				HashMap<String,Object> map = new HashMap<>();
				map.put("contact",hyOrder.getContact());
				map.put("customerNums", insuranceOrder.getPolicyHolders().size());
				map.put("orderId", hyOrder.getId());
				map.put("orderNumber", hyOrder.getOrderNumber());
				if(hyOrder.getGroupId()!=null){
					HyGroup group = hyGroupService.find(hyOrder.getGroupId());
					map.put("linePn", group.getLine().getPn());
				}
				map.put("groupStartDate", hyOrder.getFatuandate());
				map.put("groupDays", hyOrder.getTianshu());
				Store store = storeService.find(hyOrder.getStoreId());
				map.put("store", store.getStoreName());
				map.put("hyOperator",hyOrder.getOperator().getName());
				Insurance insurance = insuranceService.find(insuranceOrder.getInsuranceId());
				map.put("insuranceName", insurance.getRemark());
				map.put("type", insuranceOrder.getType());
				map.put("insuredTime", insuranceOrder.getInsuredTime());
				map.put("receivedMoney", insuranceOrder.getReceivedMoney());
				map.put("payMoney", insuranceOrder.getShifuMoney());
				map.put("profit", insuranceOrder.getProfit());
				receivedSum = receivedSum.add(insuranceOrder.getReceivedMoney());
				paySum = paySum.add(insuranceOrder.getShifuMoney());
				profitSum = profitSum.add(insuranceOrder.getProfit());
				result.add(map);
			}
			ans.put("list", result);
			ans.put("receivedSum", receivedSum);
			ans.put("paySum", paySum);
			ans.put("profitSum", profitSum);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(ans);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}

	@RequestMapping("profitToExcel/view")
	public void profitToExcel(@DateTimeFormat(pattern="yyyy-MM-dd") Date insuredStartTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date insuredEndTime,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date effectStartTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date effectEndTime,
			Integer type,Long storeId,String operator,String customerName,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try {
			//获取用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<Filter> filters = new ArrayList<>();
			if(insuredStartTime!=null){
				filters.add(Filter.ge("insuredTime", insuredStartTime));
			}
			if(insuredEndTime!=null){
				filters.add(Filter.le("insuredTime", insuredEndTime));
			}
			if(effectStartTime!=null){
				filters.add(Filter.ge("insuranceStarttime", effectStartTime));
			}
			if(effectEndTime!=null){
				filters.add(Filter.le("insuranceStarttime", effectEndTime));
			}
			if(type!=null){
				filters.add(Filter.eq("type", type));
			}
			//只统计已投保的的保单
			filters.add(Filter.eq("status", 3));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.asc("createDate"));
			List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,filters,orders);
			List<Profit> result = new ArrayList<>();
			BigDecimal receivedSum = new BigDecimal(0);
			BigDecimal paySum = new BigDecimal(0);
			BigDecimal profitSum = new BigDecimal(0);
			for(InsuranceOrder insuranceOrder:insuranceOrders){
				HyOrder hyOrder = hyOrderService.find(insuranceOrder.getOrderId());
				//先判断当前操作人权限四种权限，
				if(admin.getRole().getName().equals("门店员工") && !operator.equals(admin.getName())){
					continue;
				}
				if(admin.getRole().getName().equals("门店经理")){
					if(admin.getDepartment().getStore().getId().longValue()!=hyOrder.getStoreId().longValue())
						continue;
				}
				if(storeId!=null){		
					if(storeId.longValue()!=hyOrder.getStoreId().longValue()){
						continue;
					}
				}
				if(operator!=null && !hyOrder.getOperator().getName().contains(operator)){
					continue;
				}
				if(customerName!=null && !customerName.equals(hyOrder.getContact())){
					continue;
				}
				Profit profit = new Profit();
				profit.setContact(hyOrder.getContact());
				profit.setCustomerNums(insuranceOrder.getPolicyHolders().size());
				profit.setOrderNumber(hyOrder.getOrderNumber());
				if(hyOrder.getGroupId()!=null){
					HyGroup group = hyGroupService.find(hyOrder.getGroupId());
					profit.setLinePn(group.getLine().getPn());
				}
				profit.setGroupStartDate(hyOrder.getFatuandate());
				profit.setGroupDays(hyOrder.getTianshu());
				Store store = storeService.find(hyOrder.getStoreId());
				profit.setStore(store.getStoreName());
				profit.setHyOperator(hyOrder.getOperator().getName());
				Insurance insurance = insuranceService.find(insuranceOrder.getInsuranceId());
				profit.setInsuranceName(insurance.getRemark());
				if(insuranceOrder.getType()==0){
					profit.setType("团期投保");
				}else if(insuranceOrder.getType()==1){
					profit.setType("自主投保");
				}else if(insuranceOrder.getType()==2){
					profit.setType("网上投保");
				}else{
					profit.setType("其他投保");
				}
				profit.setInsuredTime(insuranceOrder.getInsuredTime());
				profit.setReceivedMoney(insuranceOrder.getReceivedMoney());
				profit.setPayMoney(insuranceOrder.getShifuMoney());
				profit.setProfit(insuranceOrder.getProfit());
				receivedSum = receivedSum.add(insuranceOrder.getReceivedMoney());
				paySum = paySum.add(insuranceOrder.getShifuMoney());
				profitSum = profitSum.add(insuranceOrder.getProfit());
				result.add(profit);
			}
			Profit profitZJ = new Profit();
			profitZJ.setContact("总计");
			profitZJ.setReceivedMoney(receivedSum);
			profitZJ.setPayMoney(paySum);
			profitZJ.setProfit(profitSum);
			result.add(profitZJ);
			
			StringBuilder title = new StringBuilder("保险利润表");
			baseController.export2Excel(request, response, result, "保险利润统计表.xls", title.toString(), "insuranceOrderProfit.xml");	
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("storeList")
	@ResponseBody
	public Json getStoreList(HttpSession session){
		Json json = new Json();
		try {
			//获取用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			String jpql = "select id,store_name from hy_store"; 
			List<Object[]> stores = storeService.statis(jpql);
			List<HashMap<String, Object>> result = new ArrayList<>();
			for(Object[] tmp:stores){
				BigInteger storeId = (BigInteger)tmp[0];
				if(admin.getRole().getName().contains("门店员工") || admin.getRole().getName().contains("门店经理")){
					if(admin.getDepartment().getStore().getId()!=storeId.longValue())
						continue;
				}
				HashMap<String, Object> hMap = new HashMap<>();
				hMap.put("id", (BigInteger)tmp[0]);
				hMap.put("storeName", (String)tmp[1]);
				result.add(hMap);
			}

			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
	@RequestMapping("employeeList")
	@ResponseBody
	public Json getEmployeeList(Long storeId){
		Json json = new Json();
		try {
			Store store = storeService.find(storeId);
			Set<HyAdmin> admins = store.getDepartment().getHyAdmins();
			List<HashMap<String, Object>> result = new ArrayList<>();
			for(HyAdmin admin:admins){
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("username", admin.getUsername());
				hm.put("employeeName", admin.getName());
				result.add(hm);
			}
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
	@RequestMapping("customerList")
	@ResponseBody
	public Json getCustomerList(Long orderId){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", orderId));
			InsuranceOrder insuranceOrder = insuranceOrderService.findList(null,filters,null).get(0);
			
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(insuranceOrder.getPolicyHolders());
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
	@RequestMapping("supplierList")
	@ResponseBody
	public Json getSupplierList(){
		Json json = new Json();
		try {
			String sql = "select id, supplier_name from hy_supplier";
			List<Object[]> list = hySupplierService.statis(sql);
			
			List<HashMap<String, Object>> result = new ArrayList<>();
			for(Object[] tmp:list){
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", tmp[0]);
				map.put("name", tmp[1]);
				result.add(map);
			}
			
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
	@RequestMapping("permission")
	@ResponseBody
	public Json getPermission(HttpSession session,HttpServletRequest request){
		Json json = new Json();
		try {
			//获取用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HashMap<String, Object> result = new HashMap<>();
			if(admin.getRole().getName().contains("门店员工")){
				result.put("permission", "0");
				//获取门店id
				result.put("storeId",admin.getDepartment().getStore().getId());
				result.put("operatorName", admin.getName());
			}
			else if(admin.getRole().getName().contains("门店经理")){
				result.put("permission", "1");
				//获取门店id
				result.put("storeId",admin.getDepartment().getStore().getId());
			}else{
				result.put("permission", "2");
			}		
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
}
