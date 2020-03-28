package com.hongyu.controller.liyang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.acl.Group;
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
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyPolicyHolderInfo;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.Store;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.Insurance;
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
 * 三个报表
 * 1、保险明细表
 * 2、保险利润统计表
 * 2、保险月报表
 * @author liyang
 *
 */
@Controller
@RequestMapping("/admin/insuranceStatistics/")
public class InsuranceStatisticsController {
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
	public static class Detail{
		private String contact;
		private Integer customerNums;
		private String destination;
		private String supplierName;
		private String orderNumber;
		private String linePn;
		private Date groupStartDate;
		private Integer groupDays;
		private String store;
		private String hyOperator;
		private String insuranceName;
		private String type;
		private Date insuredTime;
		private BigDecimal money = new BigDecimal(0);
		public String getContact() {
			return contact;
		}
		public void setContact(String contact) {
			this.contact = contact;
		}
		public Integer getCustomerNums() {
			return customerNums;
		}
		
		public String getSupplierName() {
			return supplierName;
		}
		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}
		public void setCustomerNums(Integer customerNums) {
			this.customerNums = customerNums;
		}	
		public String getDestination() {
			return destination;
		}
		public void setDestination(String destination) {
			this.destination = destination;
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
		public BigDecimal getMoney() {
			return money;
		}
		public void setMoney(BigDecimal money) {
			this.money = money;
		}
		
	}
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
	public static class Monthly{
		private String month;
		private BigDecimal tqReceivedMoney = new BigDecimal(0);
		private BigDecimal zzReceivedMoney = new BigDecimal(0);
		private BigDecimal wsReceivedMoney = new BigDecimal(0);
		private BigDecimal receivedSum = new BigDecimal(0);
		private BigDecimal paySum = new BigDecimal(0);
		private BigDecimal profitSum = new BigDecimal(0);
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public BigDecimal getTqReceivedMoney() {
			return tqReceivedMoney;
		}
		public void setTqReceivedMoney(BigDecimal tqReceivedMoney) {
			this.tqReceivedMoney = tqReceivedMoney;
		}
		public BigDecimal getZzReceivedMoney() {
			return zzReceivedMoney;
		}
		public void setZzReceivedMoney(BigDecimal zzReceivedMoney) {
			this.zzReceivedMoney = zzReceivedMoney;
		}
		public BigDecimal getWsReceivedMoney() {
			return wsReceivedMoney;
		}
		public void setWsReceivedMoney(BigDecimal wsReceivedMoney) {
			this.wsReceivedMoney = wsReceivedMoney;
		}
		public BigDecimal getReceivedSum() {
			return receivedSum;
		}
		public void setReceivedSum(BigDecimal receivedSum) {
			this.receivedSum = receivedSum;
		}
		public BigDecimal getPaySum() {
			return paySum;
		}
		public void setPaySum(BigDecimal paySum) {
			this.paySum = paySum;
		}
		public BigDecimal getProfitSum() {
			return profitSum;
		}
		public void setProfitSum(BigDecimal profitSum) {
			this.profitSum = profitSum;
		}
		
	}
//	@RequestMapping("detail/view")
//	@ResponseBody
//	public Json insuranceDetail(@DateTimeFormat(pattern="yyyy-MM-dd") Date insuredStartTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date insuredEndTime,
//			@DateTimeFormat(pattern="yyyy-MM-dd") Date effectStartTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date effectEndTime,
//			Integer type,Long storeId,String operator,String customerName,
//			String supplierName,HttpSession session){
//		Json json = new Json();
//		try {
//			//获取用户
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin admin = hyAdminService.find(username);		
//			List<Filter> filters = new ArrayList<>();
//			if(insuredStartTime!=null){
//				filters.add(Filter.ge("insuredTime", insuredStartTime));
//			}
//			if(insuredEndTime!=null){
//				Date endDate = DateUtil.getEndOfDay(insuredEndTime);
//				filters.add(Filter.le("insuredTime", endDate));
//			}
//			if(effectStartTime!=null){
//				filters.add(Filter.ge("insuranceStarttime", effectStartTime));
//			}
//			if(effectEndTime!=null){
//				Date endEffectDate = DateUtil.getEndOfDay(effectEndTime);
//				filters.add(Filter.le("insuranceStarttime", endEffectDate));
//			}
//			if(type!=null){
//				filters.add(Filter.eq("type", type));
//			}
//			//只统计已投保的的保单
//			filters.add(Filter.eq("status", 3));
//			List<Order> orders = new ArrayList<>();
//			// 按投保时间倒序
//			orders.add(Order.desc("insuredTime"));
//			List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,filters,orders);
//			HashMap<String, Object> ans = new HashMap<>();
//			List<HashMap<String, Object>> result = new ArrayList<>();
//			BigDecimal sum = new BigDecimal(0);
//			for(InsuranceOrder insuranceOrder:insuranceOrders){
//				HyOrder hyOrder = hyOrderService.find(insuranceOrder.getOrderId());
//				//先判断当前操作人权限四种权限，
//				if(admin.getRole().getName().contains("门店员工") && !operator.equals(admin.getName())){
//					continue;
//				}
//				
//				if(admin.getRole().getName().contains("门店经理")){
//					if(admin.getDepartment().getStore().getId().longValue()!=hyOrder.getStoreId().longValue())
//						continue;
//				}
//				if(storeId!=null){		
//					if(storeId.longValue()!=hyOrder.getStoreId().longValue()){
//						continue;
//					}
//				}
//				if(operator!=null && !hyOrder.getOperator().getName().contains(operator)){
//					continue;
//				}
//				HySupplier hySupplier = null;
//				
//				if(hyOrder.getType()==1){
////					if(hyOrder.getSupplier()!=null){
////						List<Filter> filters2 = new ArrayList<>();
////						filters2.add(Filter.eq("liable", hyOrder.getSupplier()));
////						List<HySupplierContract> contracts = hySupplierContractService.findList(null,filters2,null);
////						if(contracts!=null && contracts.size()>0){
////							hySupplier = contracts.get(0).getHySupplier();
////							
////						}					
////					}
//					HyGroup group = hyGroupService.find(hyOrder.getGroupId());
//					if(group!=null){
//						hySupplier = group.getLine().getHySupplier();
//					}
//				}
//				if(supplierName!=null){
//					if(hySupplier==null || !hySupplier.getSupplierName().contains(supplierName)){
//						continue;
//					}
//				}
//				if(customerName!=null && !hyOrder.getContact().contains(customerName)){
//					continue;
//				}
//				
//				HashMap<String,Object> map = new HashMap<>();
//				map.put("contact",hyOrder.getContact());
//				map.put("customerNums", insuranceOrder.getPolicyHolders().size());
//				map.put("orderId", hyOrder.getId());
//				map.put("orderNumber", hyOrder.getOrderNumber());
//				String destination = "";
//				if(hyOrder.getGroupId()!=null){
//					HyGroup group = hyGroupService.find(hyOrder.getGroupId());
//					map.put("linePn", group.getLine().getPn());
//					destination = group.getLine().getName();
//				}
//				map.put("supplierName", hySupplier==null?"":hySupplier.getSupplierName());
//				map.put("destination", hyOrder.getXianlumingcheng()==null?destination:hyOrder.getXianlumingcheng());
//				map.put("groupStartDate", hyOrder.getFatuandate());
//				map.put("groupDays", hyOrder.getTianshu());
//				Store store = storeService.find(hyOrder.getStoreId());
//				map.put("storeId", store.getId());
//				map.put("store", store.getStoreName());
//				map.put("hyOperator",hyOrder.getOperator().getName());
//				Insurance insurance = insuranceService.find(insuranceOrder.getInsuranceId());
//				map.put("insuranceName", insurance.getRemark());
//				map.put("type", insuranceOrder.getType());
//				map.put("insuredTime", insuranceOrder.getInsuredTime());
//				map.put("money", insuranceOrder.getReceivedMoney());
//				sum = sum.add(insuranceOrder.getReceivedMoney());
//				result.add(map);
//			}
//			ans.put("list", result);
//			ans.put("sum", sum);
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(ans);
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("查询失败："+e.getMessage());
//		}
//		return json;
//	}
	@RequestMapping("detail/view")
	@ResponseBody
	public Json insuranceDetail11(@DateTimeFormat(pattern="yyyy-MM-dd") Date insuredStartTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date insuredEndTime,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date effectStartTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date effectEndTime,
			Integer type,Long storeId,String operator,String customerName,
			String supplierName,HttpSession session,HttpServletRequest request){
		Json json = new Json();
		try {
			/**
			 * 1.确认当前的权限
			 * 2.构建sql
			 * 3.返回结果
			 */
			
			//获取用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);		
			String roleName = admin.getRole().getName();
			
			String tqsql_select = "select o.contact,o.people,o.id,o.order_number,"
					+ " o.fatuandate,o.tianshu, am.name ,o.xianlumingcheng,"
					+ "g.group_line_pn as linePn,store.id as storeId,store.store_name,"
					+ "supplier.supplier_name as supplierName,io.type,insurance.remark,"
					+ "io.insured_time,io.received_money,io.create_date ";
			String tqsql_from = "from hy_order as o,hy_insurance_order as io,"
					+ "hy_admin as am,hy_group as g,hy_line as l,hy_supplier as supplier,"
					+ "hy_insurance as insurance,hy_store as store ";
			String tqsql_where = "where io.order_id = o.id and io.status = 3 "
					+ "and io.type = 0 and io.insurance_id = insurance.id "
					+ "and o.group_id=g.id and g.line = l.id "
					+ "and l.supplier = supplier.id and o.operator_id=am.username "
					+ "and o.store_id = store.id ";
			
			String dmsql_select = "select o.contact,o.people,o.id,o.order_number, "
					+ "o.fatuandate,o.tianshu, am.name ,o.xianlumingcheng,'' as linePn,"
					+ "store.id as storeId,store.store_name,'' as supplierName,io.type,insurance.remark,"
					+ "io.insured_time,io.received_money,io.create_date ";
			String dmsql_from = "from hy_order as o,hy_insurance_order as io,"
					+ "hy_admin as am,hy_insurance as insurance,hy_store as store ";
			String dmsql_where = "where io.order_id = o.id and io.status = 3 and io.type=1 "
					+ "and io.insurance_id = insurance.id and o.operator_id=am.username "
					+ "and o.store_id = store.id ";
			StringBuilder conditions = new StringBuilder();
			/*公共可用的筛选条件*/
			if(insuredStartTime!=null){
				Date date = DateUtil.getStartOfDay(insuredStartTime);
				String starttime = DateUtil.getSimpleDate(date);
				conditions.append("and unix_timestamp(io.insured_time) >= ")
					.append("unix_timestamp('"+starttime+"') ");		
			}
			if(insuredEndTime!=null){
				Date date = DateUtil.getEndOfDay(insuredEndTime);
				String endtime = DateUtil.getSimpleDate(date);
				conditions.append("and unix_timestamp(io.insured_time) <= ")
					.append("unix_timestamp('"+endtime+"') ");	
			}
			if(effectStartTime!=null){
				Date date = DateUtil.getStartOfDay(effectStartTime);
				String starttime = DateUtil.getSimpleDate(date);
				conditions.append("and unix_timestamp(io.insurance_starttime) >= ")
					.append("unix_timestamp('"+starttime+"') ");
			}
			if(effectEndTime!=null){
				Date date = DateUtil.getEndOfDay(effectEndTime);
				String endtime = DateUtil.getSimpleDate(date);
				conditions.append("and unix_timestamp(io.insurance_starttime) <= ")
					.append("unix_timestamp('"+endtime+"') ");
			}
			if(customerName!=null){
				conditions.append("and o.contact like '%"+customerName+"%' ");
			}
			if(supplierName!=null){
				conditions.append("and supplier.supplier_name like '%"+supplierName+"'% ");
			}
			
			/*按权限分配的筛选条件*/
			if(roleName.contains("门店员工")){
				conditions.append("and am.username = '"+username+"' ");	
						
			}else if(roleName.contains("门店经理")){
				if(operator!=null){
					conditions.append("and am.name like '%"+operator+"%' ");
				}
				Long store_id = admin.getDepartment().getStore().getId();
				conditions.append("and store.id= "+store_id+" ");		
				
			}else{
				if(operator!=null){
					conditions.append("and am.name like '%"+operator+"%' ");
				}
				if(storeId!=null){
					conditions.append("and store.id = "+storeId+" ");
				}
			}
			
			
			String final_tqsql = tqsql_select+tqsql_from+tqsql_where;
			String final_dmsql = dmsql_select+dmsql_from+dmsql_where;

			String final_sql = null; 
			if(type!=null){
				if(type == 0){
					final_sql = final_tqsql+conditions.toString()+" order by io.create_date DESC";
				}else{
					final_sql = final_dmsql+conditions.toString()+" order by io.create_date DESC";
				}
			}else{
				final_sql = "select * from ("
						+ "("+final_tqsql+conditions.toString()+") union"
						+ "("+final_dmsql+conditions.toString()+") "
						+ ") as h order by h.create_date DESC";
			}
			
			System.out.println("final_sql = "+final_sql);
			List<Object[]> list = insuranceOrderService.statis(final_sql);
			HashMap<String, Object> ans = new HashMap<>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			
			BigDecimal sum = new BigDecimal(0);
			for(Object[] objects:list){
					
				HashMap<String,Object> map = new HashMap<>();
				map.put("contact",objects[0]);
				map.put("customerNums", objects[1]);
				map.put("orderId", objects[2]);
				map.put("orderNumber", objects[3]);
				map.put("groupStartDate", objects[4]);
				map.put("groupDays", objects[5]);
				map.put("hyOperator",objects[6]);
				map.put("destination",objects[7]);
				
				map.put("linePn", objects[8]);
				map.put("storeId", objects[9]);
				map.put("store", objects[10]);
				map.put("supplierName", objects[11]);
				map.put("type", objects[12]);
				map.put("insuranceName", objects[13]);
				map.put("insuredTime", objects[14]);
				map.put("money", objects[15]);
				map.put("createTime", objects[16]);
				sum = sum.add((BigDecimal)objects[15]);
				result.add(map);
			}
			ans.put("list", result);
			ans.put("sum", sum);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(ans);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
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
	@RequestMapping("monthly/view")
	@ResponseBody
	public Json insuranceMonthly(@RequestParam(required=true) @DateTimeFormat(pattern="yyyy-MM") Date startTime,@RequestParam(required=true) @DateTimeFormat(pattern="yyyy-MM") Date endTime){
		Json json = new Json();
		try {
			DateFormat dFormat = new SimpleDateFormat("yyyy-MM");
			String st = "2000-01";
			String et = "3000-01";
			if(startTime!=null){
				st = dFormat.format(startTime);
			}
			if(endTime!=null){
				et = dFormat.format(endTime);
			}	
			List<HashMap<String, Object>> result = new ArrayList<>();
			while(st.compareTo(et)<=0){
				HashMap<String, Object> map = new HashMap<>();
				map.put("month", st);
				//求团期投保总额
				String sql = "select received_money,shifu_money,profit from hy_insurance_order where date_format(insured_time,'%Y-%m')='"
							+st+"' and type=0 and status=3";
				//System.out.println(sql);
				List<Object[]> tqList = insuranceOrderService.statis(sql);
				BigDecimal tqReceivedMoney = new BigDecimal(0);
				BigDecimal tqPayMoney = new BigDecimal(0);
				BigDecimal tqProfitMoney = new BigDecimal(0);
				if(!tqList.isEmpty() && tqList.size()>0 && tqList.get(0)!=null){
					for(Object[] tmp:tqList){
						tqReceivedMoney = tqReceivedMoney.add((BigDecimal)tmp[0]);
						tqPayMoney = tqPayMoney.add((BigDecimal)tmp[1]);
						tqProfitMoney = tqProfitMoney.add((BigDecimal)tmp[2]);
					}
				}
				
				//求自主投保总额
				String sql1 = "select received_money,shifu_money,profit from hy_insurance_order where date_format(insured_time,'%Y-%m')='"
							+st+"' and type=1 and status=3";
				//System.out.println(sql1);
				List<Object[]> zzList = insuranceOrderService.statis(sql1);
				BigDecimal zzReceivedMoney = new BigDecimal(0);
				BigDecimal zzPayMoney = new BigDecimal(0);
				BigDecimal zzProfitMoney = new BigDecimal(0);
				if(!zzList.isEmpty() && zzList.size()>0 && zzList.get(0)!=null){
					for(Object[] tmp:zzList){
						zzReceivedMoney = zzReceivedMoney.add((BigDecimal)tmp[0]);
						zzPayMoney = zzPayMoney.add((BigDecimal)tmp[1]);
						zzProfitMoney = zzProfitMoney.add((BigDecimal)tmp[2]);
					}
				}	
				//求网上投保总额
				String sql2 = "select received_money,shifu_money,profit from hy_insurance_order where date_format(insured_time,'%Y-%m')='"
							+st+"' and type=2 and status=3";
				//System.out.println(sql2);
				List<Object[]> wsList = insuranceOrderService.statis(sql2);
				BigDecimal wsReceivedMoney = new BigDecimal(0);
				BigDecimal wsPayMoney = new BigDecimal(0);
				BigDecimal wsProfitMoney = new BigDecimal(0);
				if(!wsList.isEmpty() && wsList.size()>0 && wsList.get(0)!=null){
					for(Object[] tmp:wsList){
						wsReceivedMoney = wsReceivedMoney.add((BigDecimal)tmp[0]);
						wsPayMoney = wsPayMoney.add((BigDecimal)tmp[1]);
						wsProfitMoney = wsProfitMoney.add((BigDecimal)tmp[2]);
					}
				}		
				map.put("tqReceivedSum", tqReceivedMoney);
				map.put("zzReceivedSum", zzReceivedMoney);
				map.put("wsReceivedSum", wsReceivedMoney);
				map.put("receivedSum", tqReceivedMoney.add(zzReceivedMoney).add(wsReceivedMoney));
				map.put("paySum", tqPayMoney.add(zzPayMoney).add(wsPayMoney));
				map.put("profitSum", tqProfitMoney.add(zzProfitMoney).add(wsProfitMoney));
				result.add(map);
				st = DateUtil.getNextMonth(st);
			}
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setMsg("查询失败："+e.getMessage());
			json.setSuccess(false);
		}
		return json;
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
	@RequestMapping("detailToExcel/view")
	@ResponseBody
	public void detailToExcel(@DateTimeFormat(pattern="yyyy-MM-dd") Date insuredStartTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date insuredEndTime,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date effectStartTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date effectEndTime,
			Integer type,Long storeId,String operator,String customerName,
			String supplierName,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try {
			//获取用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);		
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
			List<Detail> result = new ArrayList<>();
			BigDecimal sum = new BigDecimal(0);
			for(int i=0;i<insuranceOrders.size();i++){
				InsuranceOrder insuranceOrder = insuranceOrders.get(i);
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
				HySupplier hySupplier = null;
				
				if(hyOrder.getType()==1){
					if(hyOrder.getSupplier()!=null){
						List<Filter> filters2 = new ArrayList<>();
						filters2.add(Filter.eq("liable", hyOrder.getSupplier()));
						List<HySupplierContract> contracts = hySupplierContractService.findList(null,filters2,null);
						if(contracts!=null && contracts.size()>0){
							hySupplier = contracts.get(0).getHySupplier();
							
						}					
					}			
				}
				if(supplierName!=null){
					if(hySupplier==null || !hySupplier.getSupplierName().contains(supplierName)){
						continue;
					}
				}	
				if(customerName!=null && !customerName.equals(hyOrder.getContact())){
					continue;
				}
				Detail detail = new Detail();
				detail.setContact(hyOrder.getContact());
				//System.out.println(detail.getContact());
				detail.setCustomerNums(insuranceOrder.getPolicyHolders().size());
				detail.setOrderNumber(hyOrder.getOrderNumber());
				String destination = "";
				if(hyOrder.getGroupId()!=null){
					HyGroup group = hyGroupService.find(hyOrder.getGroupId());
					detail.setLinePn(group.getLine().getPn());
					destination = group.getLine().getName();
				}
				detail.setDestination(hyOrder.getXianlumingcheng()==null?destination:hyOrder.getXianlumingcheng());
				detail.setSupplierName(hySupplier==null?"":hySupplier.getSupplierName());
				detail.setGroupStartDate(hyOrder.getFatuandate());
				detail.setGroupDays(hyOrder.getTianshu());
				Store store = storeService.find(hyOrder.getStoreId());
				detail.setStore(store.getStoreName());
				detail.setHyOperator(hyOrder.getOperator().getName());
				Insurance insurance = insuranceService.find(insuranceOrder.getInsuranceId());
				detail.setInsuranceName(insurance.getRemark());
				if(insuranceOrder.getType()==0){
					detail.setType("团期投保");
				}else if(insuranceOrder.getType()==1){
					detail.setType("自主投保");
				}else if(insuranceOrder.getType()==2){
					detail.setType("网上投保");
				}else{
					detail.setType("其他投保");
				}
				detail.setInsuredTime(insuranceOrder.getInsuredTime());
				detail.setMoney(insuranceOrder.getReceivedMoney());
				sum = sum.add(insuranceOrder.getReceivedMoney());
				result.add(detail);
				
			}
			Detail sumDetail = new Detail();
			sumDetail.setContact("总计");
			sumDetail.setMoney(sum);
			result.add(sumDetail);
			StringBuilder title = new StringBuilder("保险明细表");
			baseController.export2Excel(request, response, result, "保险明细统计表.xls", title.toString(), "insuranceOrderDetail.xml");	
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	@RequestMapping("monthlyToExcel/view")
	public void monthlyToExcel(@RequestParam(required=true) @DateTimeFormat(pattern="yyyy-MM") Date startTime,@RequestParam(required=true) @DateTimeFormat(pattern="yyyy-MM") Date endTime,
							HttpServletResponse response,HttpServletRequest request){
		try {
			DateFormat dFormat = new SimpleDateFormat("yyyy-MM");
			String st = "2000-01";
			String et = "3000-01";
			if(startTime!=null){
				st = dFormat.format(startTime);
			}
			if(endTime!=null){
				et = dFormat.format(endTime);
			}	
			List<Monthly> result = new ArrayList<>();
			while(st.compareTo(et)<=0){
				Monthly monthly = new Monthly();
				monthly.setMonth(st);
				//求团期投保总额
				String sql = "select received_money,shifu_money,profit from hy_insurance_order where date_format(insured_time,'%Y-%m')='"
							+st+"' and type=0 and status=3";
				//System.out.println(sql);
				List<Object[]> tqList = insuranceOrderService.statis(sql);
				BigDecimal tqReceivedMoney = new BigDecimal(0);
				BigDecimal tqPayMoney = new BigDecimal(0);
				BigDecimal tqProfitMoney = new BigDecimal(0);
				if(!tqList.isEmpty() && tqList.size()>0 && tqList.get(0)!=null){
					for(Object[] tmp:tqList){
						tqReceivedMoney = tqReceivedMoney.add((BigDecimal)tmp[0]);
						tqPayMoney = tqPayMoney.add((BigDecimal)tmp[1]);
						tqProfitMoney = tqProfitMoney.add((BigDecimal)tmp[2]);
					}
				}
				
				//求自主投保总额
				String sql1 = "select received_money,shifu_money,profit from hy_insurance_order where date_format(insured_time,'%Y-%m')='"
							+st+"' and type=1 and status=3";
				//System.out.println(sql1);
				List<Object[]> zzList = insuranceOrderService.statis(sql1);
				BigDecimal zzReceivedMoney = new BigDecimal(0);
				BigDecimal zzPayMoney = new BigDecimal(0);
				BigDecimal zzProfitMoney = new BigDecimal(0);
				if(!zzList.isEmpty() && zzList.size()>0 && zzList.get(0)!=null){
					for(Object[] tmp:zzList){
						zzReceivedMoney = zzReceivedMoney.add((BigDecimal)tmp[0]);
						zzPayMoney = zzPayMoney.add((BigDecimal)tmp[1]);
						zzProfitMoney = zzProfitMoney.add((BigDecimal)tmp[2]);
					}
				}	
				//求网上投保总额
				String sql2 = "select received_money,shifu_money,profit from hy_insurance_order where date_format(insured_time,'%Y-%m')='"
							+st+"' and type=2 and status=3";
				//System.out.println(sql2);
				List<Object[]> wsList = insuranceOrderService.statis(sql2);
				BigDecimal wsReceivedMoney = new BigDecimal(0);
				BigDecimal wsPayMoney = new BigDecimal(0);
				BigDecimal wsProfitMoney = new BigDecimal(0);
				if(!wsList.isEmpty() && wsList.size()>0 && wsList.get(0)!=null){
					for(Object[] tmp:wsList){
						wsReceivedMoney = wsReceivedMoney.add((BigDecimal)tmp[0]);
						wsPayMoney = wsPayMoney.add((BigDecimal)tmp[1]);
						wsProfitMoney = wsProfitMoney.add((BigDecimal)tmp[2]);
					}
				}	
				
				monthly.setTqReceivedMoney(tqReceivedMoney);
				monthly.setZzReceivedMoney(zzReceivedMoney);
				monthly.setWsReceivedMoney(wsReceivedMoney);
				monthly.setReceivedSum(tqReceivedMoney.add(zzReceivedMoney).add(wsReceivedMoney));
				monthly.setPaySum(tqPayMoney.add(zzPayMoney).add(wsPayMoney));
				monthly.setProfitSum(tqProfitMoney.add(zzProfitMoney).add(wsProfitMoney));
				result.add(monthly);
				st = DateUtil.getNextMonth(st);
			}
			StringBuilder title = new StringBuilder("保险月汇总表");
			title.append("("+dFormat.format(startTime)+" ~ "+dFormat.format(endTime)+")");
			baseController.export2Excel(request, response, result, "保险月汇总统计表.xls", title.toString(), "insuranceOrderMonthly.xml");	
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
