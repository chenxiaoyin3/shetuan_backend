package com.hongyu.controller.wj;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Branch;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.dao.BiangengkoudianDao;
import com.hongyu.dao.ProfitShareConfirmDetailDao;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.InsuranceMonth;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.Store;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.StoreService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.liyang.EmployeeUtil;
import com.sun.org.apache.xml.internal.utils.ObjectPool;

/**
 * 直营门店利润统计
 * 门店员工可看
 */

@Controller
//@RequestMapping("/directstoreprofitstatics")
@RequestMapping("/admin/directstore_employee")
public class DirectStoreProfitStatics {
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	
	@RequestMapping("/employee")
	@ResponseBody
	public Json employee(String startDay, String endDay,HttpSession session){
		Json json = new Json();
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		
		try {
//			Date start = format.parse(startDay);
//			Date end = format.parse(endDay);
//			System.out.println(start);
//			System.out.println(end);
			
			//先 只统计线路  （若统计所有  去掉o.type = 1
			
			//实收款
			HashMap<String, Object> map = new HashMap<String, Object>();
			String jpql = "select sum(r.money) from hy_order as o,hy_receipt_refund as r "
					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
					+"'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
					+"' and o.id = r.order_id and r.type = 0 and o.type = 1 and o.status = 3 and o.creator_id = '"+username+"'";
             
			System.out.println(jpql);
			List<Object[]> list = hyOrderService.statis(jpql);
			BigDecimal income = new BigDecimal(0);
			if (!list.isEmpty() &&list.size()!=0&& list.get(0)!=null) {
				Object iObject = list.get(0);
				income = new BigDecimal(iObject.toString());
//				BigInteger income = new BigInteger(iObject.toString());
			}
			
//			//返利
//			String fanli = "select sum(b.amount) from hy_branch_pre_save as b,hy_order as o "
//					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//					+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//					+ "' and o.id=b.order_id and b.type=5 and o.creator_id = '"+username+"'";
//			List<Object[]> fanlilist = hyOrderService.statis(fanli);
//			BigDecimal incomefanli = new BigDecimal(0);
//			if (!fanlilist.isEmpty() &&fanlilist.size()!=0&& fanlilist.get(0)!=null) {
//				Object iObject = list.get(0);
//				incomefanli = new BigDecimal(iObject.toString());
//			} 
//			map.put("income", income.add(incomefanli));
			map.put("income", income);
			
			//实退款
			String jpql2 = "select sum(r.money) from hy_order as o,hy_receipt_refund as r "
					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
					+"'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
					+"' and o.id = r.order_id and r.type = 1 and o.type = 1 and o.status = 3 and o.creator_id = '"+username+"'";
             
			System.out.println(jpql2);
			List<Object[]> list2 = hyOrderService.statis(jpql2);
			BigDecimal refund = new BigDecimal(0);
			if (!list2.isEmpty() &&list2.size()!=0&& list2.get(0)!=null) {
				Object iObject = list2.get(0);
				refund = new BigDecimal(iObject.toString());
			}
			
			//增值业务供应商付款 
			String jpql3 = "select sum(a.money) from hy_order as o , hy_added_service as a "
					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
					+"' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
					+"' and o.id = a.order_id and o.status = 3 and o.type = 1 and o.creator_id = '"+username+"'";
			
			System.out.println(jpql3);
			List<Object[]> list3 = hyOrderService.statis(jpql3);
			BigDecimal refund2 = new BigDecimal(0);
			if (!list3.isEmpty() &&list3.size()!=0&& list3.get(0)!=null) {
				Object iObject = list3.get(0);
				refund2 = new BigDecimal(iObject.toString());
			}
			
			//保险 + 订单结算价
//			String jpql4 = "select sum(i.shifu_money) from hy_order as o , hy_insurance_order as i "
//					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//					+"' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//					+"' and o.id=i.order_id and o.creator_id = '"+username+"'";
//			String jpql4 = "select sum(c.settlement_price) from hy_order o , hy_order_customer c, hy_order_item i "
//					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//					+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//					+ "' and i.order_id=o.id and c.item_id = i.id and c.is_insurance=1"
//					+ " and o.creator_id = '"+username+"'";
			String jpql4 = "select sum(o.jiusuan_money  - ifnull(o.discounted_price,0) - ifnull(o.store_fan_li,0) + o.tip) from hy_order as o " 
					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
					+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay 
					+ "' and o.status = 3 and o.type = 1 and  o.creator_id = '"+username+"'";
			
			
			System.out.println(jpql4);
			List<Object[]> list4 = hyOrderService.statis(jpql4);
			BigDecimal refund3 = new BigDecimal(0);
			if (!list4.isEmpty() &&list4.size()!=0&& list4.get(0)!=null) {
				Object iObject = list4.get(0);
				refund3 = new BigDecimal(iObject.toString());
			}
			//支出
			BigDecimal outcome = new BigDecimal(0);
			outcome = outcome.add(refund).add(refund2).add(refund3);
			map.put("outcome", outcome);
			
			BigDecimal profit = new BigDecimal(0);
			BigDecimal profitRate = new BigDecimal(0);
			BigDecimal zero = new BigDecimal("0.0");
			profit = income.subtract(outcome);
			if(income.compareTo(zero)!=0){
				profitRate = profit.divide(income,3,BigDecimal.ROUND_HALF_UP);
			}
			map.put("profit",profit);
			map.put("profitRate",profitRate);
			List<HashMap<String, Object>> res = new ArrayList<>();
			res.add(map);
			
			json.setObj(res);
			
			json.setMsg("查询成功");
			json.setSuccess(true);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json.setMsg("查询失败");
			json.setSuccess(false);
		}

		return json;
		
	}
	
	
	
	@RequestMapping("/download/employee")
	public void downloademployee(String startDay, String endDay,HttpSession session,HttpServletRequest request,HttpServletResponse response){
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		
		try {
			EmployeeProfit employeeProfit = new EmployeeProfit();
//			Date start = format.parse(startDay);
//			Date end = format.parse(endDay);
//			System.out.println(start);
//			System.out.println(end);
			
			//实收款
			HashMap<String, Object> map = new HashMap<String, Object>();
			String jpql = "select sum(r.money) from hy_order as o,hy_receipt_refund as r "
					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
					+"'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
					+"' and o.id = r.order_id and r.type = 0 and o.type = 1 and o.status = 3 and o.creator_id = '"+username+"'";
             
			System.out.println(jpql);
			List<Object[]> list = hyOrderService.statis(jpql);
			BigDecimal income = new BigDecimal(0);
			if (!list.isEmpty() &&list.size()!=0&& list.get(0)!=null) {
				Object iObject = list.get(0);
				income = new BigDecimal(iObject.toString());
			} 
			
//			//返利
//			String fanli = "select sum(b.amount) from hy_branch_pre_save as b,hy_order as o "
//					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//					+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//					+ "' and o.id=b.order_id and b.type=5 and o.creator_id = '"+username+"'";
//			List<Object[]> fanlilist = hyOrderService.statis(fanli);
//			BigDecimal incomefanli = new BigDecimal(0);
//			if (!fanlilist.isEmpty() &&fanlilist.size()!=0&& fanlilist.get(0)!=null) {
//				Object iObject = list.get(0);
//				incomefanli = new BigDecimal(iObject.toString());
//			} 
//			income = income.add(incomefanli);
			map.put("income", income);
			
			employeeProfit.setIncome(income);
			
			
			//实退款
			String jpql2 = "select sum(r.money) from hy_order as o,hy_receipt_refund as r "
					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
					+"'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
					+"' and o.id = r.order_id and r.type = 1 and o.type = 1 and o.status = 3 and o.creator_id = '"+username+"'";
             
			System.out.println(jpql2);
			List<Object[]> list2 = hyOrderService.statis(jpql2);
			BigDecimal refund = new BigDecimal(0);
			if (!list2.isEmpty() &&list2.size()!=0&& list2.get(0)!=null) {
				Object iObject = list2.get(0);
				refund = new BigDecimal(iObject.toString());
			}
			
			//增值业务供应商付款 
			String jpql3 = "select sum(a.money) from hy_order as o , hy_added_service as a "
					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
					+"' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
					+"' and o.id = a.order_id and o.status = 3 and o.type = 1 and o.creator_id = '"+username+"'";
			
			System.out.println(jpql3);
			List<Object[]> list3 = hyOrderService.statis(jpql3);
			BigDecimal refund2 = new BigDecimal(0);
			if (!list3.isEmpty() &&list3.size()!=0&& list3.get(0)!=null) {
				Object iObject = list3.get(0);
				refund2 = new BigDecimal(iObject.toString());
			}
			
			//订单支付金额（结算价（含保险）-优惠-返利+小费）
			String jpql4 = "select sum(o.jiusuan_money  - ifnull(o.discounted_price,0) - ifnull(o.store_fan_li,0) + o.tip) from hy_order as o " 
					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
					+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay 
					+ "' and o.status = 3 and o.type = 1 and  o.creator_id = '"+username+"'";
			
			System.out.println(jpql4);
			List<Object[]> list4 = hyOrderService.statis(jpql4);
			BigDecimal refund3 = new BigDecimal(0);
			if (!list4.isEmpty() &&list4.size()!=0&& list4.get(0)!=null) {
				Object iObject = list4.get(0);
				refund3 = new BigDecimal(iObject.toString());
			}
			//支出
			BigDecimal outcome = new BigDecimal(0);
			outcome = outcome.add(refund).add(refund2).add(refund3);
			map.put("outcome", outcome);
			employeeProfit.setOutcome(outcome);
			
			BigDecimal profit = new BigDecimal(0);
			BigDecimal profitRate = new BigDecimal(0);
			BigDecimal zero = new BigDecimal("0.0");
			profit = income.subtract(outcome);
			if(income.compareTo(zero)!=0){
//				profit = income.subtract(outcome);
				profitRate = profit.divide(income,3,BigDecimal.ROUND_HALF_UP);
			}
			map.put("profit",profit);
			map.put("profitRate",profitRate);
			employeeProfit.setProfit(profit);
			employeeProfit.setProfitRate(profitRate);
			
			List<HashMap<String, Object>> res = new ArrayList<>();
			res.add(map);
			
			List<EmployeeProfit> employeeProfits = new ArrayList<>();
			employeeProfits.add(employeeProfit);
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("利润统计表（员工）");
			String fileName = "员工利润统计表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "employeeProfitStatistics.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, employeeProfits, fileName, tableTitle, configFile);
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@RequestMapping("/detail")
	@ResponseBody
	public Json employeeProfitDetail(String startDay,String endDay,HttpSession session){
		Json json = new Json();
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		List<ProfitDetail> profitDetails = new ArrayList<>();
		try {
			
			//暂时只统计线路
			String jpql = "select id from hy_order as o "
					+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
					+"' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
					+"' and o.status = 3 and o.type = 1 and o.creator_id = '"+username+"'";
             
			System.out.println(jpql);
			List<Object[]> list = hyOrderService.statis(jpql);
			if (!list.isEmpty() &&list.size()!=0&& list.get(0)!=null) {
				for(Object iObject : list){
//					Object iObject = list.get(0);
					Long orderId = Long.valueOf(String.valueOf(iObject));
					HyOrder order = hyOrderService.find(orderId);
					HyGroup group = hyGroupService.find(order.getGroupId());
					HyLine line = group.getLine();
					
					ProfitDetail profitDetail = new ProfitDetail();
					profitDetail.setOrderSn(order.getOrderNumber());
					profitDetail.setProductId(line.getPn());
					profitDetail.setProductName(line.getName());
					profitDetail.setType(group.getTeamType()?1:0);
					profitDetail.setFatuanDate(order.getFatuandate());
					profitDetail.setOrderDate(order.getCreatetime());
					profitDetail.setContractName(order.getContact());
					profitDetail.setPeople(order.getPeople());
					
					//订单支付金额
					BigDecimal money = new BigDecimal(0);
					//+结算价 + 保险
					if(order.getJiusuanMoney()!=null && order.getJiusuanMoney().compareTo(BigDecimal.ZERO)!=0){
						money  = money.add(order.getJiusuanMoney());
					}
					//-优惠金额
					if(order.getDiscountedPrice() != null && order.getDiscountedPrice().compareTo(BigDecimal.ZERO)!=0){
						money  = money.subtract(order.getDiscountedPrice());
					}
					//-门店返利
					if(order.getStoreFanLi()!=null && order.getStoreFanLi().compareTo(BigDecimal.ZERO)!=0){
						money  = money.subtract(order.getStoreFanLi());
					}
					//+小费
					if(order.getTip()!=null && order.getTip().compareTo(BigDecimal.ZERO)!=0){
						money  = money.add(order.getTip());
					}
					profitDetail.setMoney(money);
					
					
					//该订单实收款
					String incomeSql = "select sum(r.money) from hy_receipt_refund  as  r where  r.type = 0 and r.order_id = " + orderId;
					List<Object[]> incomes = hyOrderService.statis(incomeSql);
					
					BigDecimal income = new BigDecimal(0);
					if (!incomes.isEmpty() &&incomes.size()!=0&& incomes.get(0)!=null) {
						Object i = incomes.get(0);
						income = new BigDecimal(i.toString());					
					}
					profitDetail.setReceiptMoney(income);
					
					//保险金额
					BigDecimal insuranceMoney = new BigDecimal(0);
					if(order.getJiesuanMoney1().compareTo(BigDecimal.ZERO)!=0){
						insuranceMoney = order.getJiusuanMoney().subtract(order.getJiesuanMoney1());
					}
					
					profitDetail.setSupplierName(line.getHySupplier().getSupplierName());
					profitDetail.setSupplierMoney(money.subtract(insuranceMoney));  //供应商结算价 = 订单支付金额 - 保险
					profitDetail.setInsuranceMoney(insuranceMoney);
					
					
					//增值服务
					BigDecimal addMoney = new BigDecimal(0);
					String addedSql = "select sum(a.money) from hy_added_service as a where a.status=3 and order_id = " + orderId;
					List<Object[]> adds = hyOrderService.statis(addedSql);
					if (!adds.isEmpty() &&adds.size()!=0&& adds.get(0)!=null) {
						Object i = adds.get(0);
						addMoney = new BigDecimal(i.toString());					
					}
					profitDetail.setAddMoney(addMoney);
					profitDetail.setAdjustMoney(order.getAdjustMoney());
					
					//实退款
					BigDecimal refundMoney = new BigDecimal(0);
					String refundSql = "select sum(r.money) from hy_receipt_refund  as  r where  r.type = 1 and r.order_id = " + orderId;
					List<Object[]> refunds = hyOrderService.statis(refundSql);
					if (!refunds.isEmpty() &&refunds.size()!=0&& refunds.get(0)!=null) {
						Object i = refunds.get(0);
						refundMoney = new BigDecimal(i.toString());					
					}
					profitDetail.setRefundMoney(refundMoney);
					
					//利润 = 订单支付金额 - 增值业务金额 - 实退款
					BigDecimal profit = income.subtract(money).subtract(addMoney).subtract(refundMoney);
					BigDecimal profitRate = new BigDecimal("0.0");
//					BigDecimal zero = new BigDecimal("0.0");
					if(income.compareTo(BigDecimal.ZERO)!=0){
						profitRate = profit.divide(income,3,BigDecimal.ROUND_HALF_UP);
					}
					profitDetail.setProfit(profit);
					profitDetail.setProfitRate(profitRate);	
					profitDetails.add(profitDetail);
				}
				

				
			}
			json.setObj(profitDetails);
			json.setMsg("查询成功");
			json.setSuccess(true);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json.setMsg("查询失败");
			json.setSuccess(false);
			
		}
		return json;
		
	}
	
	
	public class EmployeeProfit{
		BigDecimal income;
		BigDecimal outcome;
		BigDecimal profit;
		BigDecimal profitRate;
		public BigDecimal getIncome() {
			return income;
		}
		public void setIncome(BigDecimal income) {
			this.income = income;
		}
		public BigDecimal getOutcome() {
			return outcome;
		}
		public void setOutcome(BigDecimal outcome) {
			this.outcome = outcome;
		}
		public BigDecimal getProfit() {
			return profit;
		}
		public void setProfit(BigDecimal profit) {
			this.profit = profit;
		}
		public BigDecimal getProfitRate() {
			return profitRate;
		}
		public void setProfitRate(BigDecimal profitRate) {
			this.profitRate = profitRate;
		}
	}
	
	
	public class ProfitDetail{
		String orderSn;  //订单编号
		String productId; //订单id
		String productName;   //产品名称
		Integer type; //产品类型（散客、团队）
		Date fatuanDate; //（发团日期）
		Date orderDate;  //下单日期
		String contractName; //联系人
		Integer people; //人数
		BigDecimal money;  //订单金额
		
		BigDecimal receiptMoney; // 实收款
		String supplierName; //供应商名称
		BigDecimal supplierMoney;//供应商结算价
		BigDecimal insuranceMoney;  //保险金额
		BigDecimal addMoney;//增值服务金额
		BigDecimal adjustMoney;// 调整金额
		BigDecimal refundMoney; //实退款
		
		BigDecimal profit; //利润
		BigDecimal profitRate;//利润率
		public String getOrderSn() {
			return orderSn;
		}
		public void setOrderSn(String orderSn) {
			this.orderSn = orderSn;
		}
		
		public String getProductId() {
			return productId;
		}
		public void setProductId(String productId) {
			this.productId = productId;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		
		public Integer getType() {
			return type;
		}
		public void setType(Integer type) {
			this.type = type;
		}
		public Date getFatuanDate() {
			return fatuanDate;
		}
		public void setFatuanDate(Date fatuanDate) {
			this.fatuanDate = fatuanDate;
		}
		public Date getOrderDate() {
			return orderDate;
		}
		public void setOrderDate(Date orderDate) {
			this.orderDate = orderDate;
		}
		public String getContractName() {
			return contractName;
		}
		public void setContractName(String contractName) {
			this.contractName = contractName;
		}
		
		public Integer getPeople() {
			return people;
		}
		public void setPeople(Integer people) {
			this.people = people;
		}
		public BigDecimal getMoney() {
			return money;
		}
		public void setMoney(BigDecimal money) {
			this.money = money;
		}
		public BigDecimal getReceiptMoney() {
			return receiptMoney;
		}
		public void setReceiptMoney(BigDecimal receiptMoney) {
			this.receiptMoney = receiptMoney;
		}
		public String getSupplierName() {
			return supplierName;
		}
		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}
		public BigDecimal getInsuranceMoney() {
			return insuranceMoney;
		}
		public void setInsuranceMoney(BigDecimal insuranceMoney) {
			this.insuranceMoney = insuranceMoney;
		}
		public BigDecimal getAddMoney() {
			return addMoney;
		}
		public void setAddMoney(BigDecimal addMoney) {
			this.addMoney = addMoney;
		}
		public BigDecimal getAdjustMoney() {
			return adjustMoney;
		}
		public void setAdjustMoney(BigDecimal adjustMoney) {
			this.adjustMoney = adjustMoney;
		}
		public BigDecimal getProfit() {
			return profit;
		}
		public void setProfit(BigDecimal profit) {
			this.profit = profit;
		}
		public BigDecimal getProfitRate() {
			return profitRate;
		}
		public void setProfitRate(BigDecimal profitRate) {
			this.profitRate = profitRate;
		}
		public BigDecimal getSupplierMoney() {
			return supplierMoney;
		}
		public void setSupplierMoney(BigDecimal supplierMoney) {
			this.supplierMoney = supplierMoney;
		}
		public BigDecimal getRefundMoney() {
			return refundMoney;
		}
		public void setRefundMoney(BigDecimal refundMoney) {
			this.refundMoney = refundMoney;
		}
		
		
		
		
		
	}

}
