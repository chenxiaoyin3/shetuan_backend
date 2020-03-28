package com.hongyu.controller.wj;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.StoreService;

/**
 * 直营门店利润统计
 * 门店经理可看
 */
@Controller
@RequestMapping("/admin/directstore_manager")
public class DirectStoreProfitStatics2 {

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
	
	
	@RequestMapping("/manager")
	@ResponseBody
	public Json manager(String startDay, String endDay,HttpSession session,HttpServletRequest request){
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Department store = admin.getDepartment();
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("department", store));
			List<HyAdmin> admins = hyAdminService.findList(null,filters,null);
			
//			/**
//			 * 获取用户权限范围
//			 */
//			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
//			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
//			/** 将数据按照名字排序 */
//			List<Order> orders = new ArrayList<Order>();
//			Order order = Order.desc("createDate");
//			orders.add(order);
//
//			/** 数据按照创建人筛选 */
//			List<Filter> filters = new ArrayList<Filter>();
//			Filter filter = Filter.in("hyAdmin", hyAdmins);
//			filters.add(filter);
//
//			Pageable pageable = new Pageable();
//			pageable.setFilters(filters);
//			pageable.setOrders(orders);
//			
//
//			/** 找到分页的员工数据 */
//			Page<HyAdmin> page = hyAdminService.findPage(pageable);

			/** 遍历当前页员工数据，返回前端需要的数据格式 */
			for (HyAdmin employee : admins) {
				String name = employee.getUsername();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("username", employee.getName());
				
				//实收款
				
				String jpql = "select sum(r.money) from hy_order as o,hy_receipt_refund as r "
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+"'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
						+"' and o.id = r.order_id and r.type = 0 and o.type =1 and o.status = 3 and o.creator_id = '"+name+"'";
	             
				System.out.println(jpql);
				List<Object[]> list = hyOrderService.statis(jpql);
				BigDecimal income = new BigDecimal(0);
				if (!list.isEmpty() &&list.size()!=0&& list.get(0)!=null) {
					Object iObject = list.get(0);
					income = new BigDecimal(iObject.toString());
				}
				
//				//返利
//				String fanli = "select sum(b.amount) from hy_branch_pre_save as b,hy_order as o "
//						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//						+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//						+ "' and o.id=b.order_id and b.type=5 and o.creator_id = '"+name+"'";
//				List<Object[]> fanlilist = hyOrderService.statis(fanli);
//				BigDecimal incomefanli = new BigDecimal(0);
//				if (!fanlilist.isEmpty() &&fanlilist.size()!=0&& fanlilist.get(0)!=null) {
//					Object iObject = list.get(0);
//					incomefanli = new BigDecimal(iObject.toString());
//				} 
//				map.put("income", income.add(incomefanli));
				map.put("income", income);
				
				
				//实退款
				String jpql2 = "select sum(r.money) from hy_order as o,hy_receipt_refund as r "
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+"'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
						+"' and o.id = r.order_id and r.type = 1 and o.type = 1 and o.status = 3 and o.creator_id = '"+name+"'";
	             
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
						+"' and o.id = a.order_id  and o.type = 1 and o.status = 3 and o.creator_id = '"+name+"'";
				
				System.out.println(jpql3);
				List<Object[]> list3 = hyOrderService.statis(jpql3);
				BigDecimal refund2 = new BigDecimal(0);
				if (!list3.isEmpty() &&list3.size()!=0&& list3.get(0)!=null) {
					Object iObject = list3.get(0);
					refund2 = new BigDecimal(iObject.toString());
				}
				
				//保险
//				String jpql4 = "select sum(i.shifu_money) from hy_order as o , hy_insurance_order as i "
//						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//						+"' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//						+"' and o.id=i.order_id and o.creator_id = '"+name+"'";
				
//				String jpql4 = "select sum(c.settlement_price) from hy_order o , hy_order_customer c, hy_order_item i "
//						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//						+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//						+ "' and i.order_id=o.id and c.item_id = i.id and c.is_insurance=1"
//						+ " and o.creator_id = '"+name+"'";
				String jpql4 = "select sum(o.jiusuan_money  - ifnull(o.discounted_price,0) - ifnull(o.store_fan_li,0) + o.tip) from hy_order as o " 
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay 
						+ "' and o.status = 3 and o.type = 1 and  o.creator_id = '"+name+"'";
				
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
//					profit = income.subtract(outcome);
					profitRate = profit.divide(income,3,BigDecimal.ROUND_HALF_UP);
				}
				map.put("profit",profit);
				map.put("profitRate",profitRate);
				
				result.add(map);
						
			}
			
			json.setSuccess(true);
			json.setMsg("查找成功！");
			json.setObj(result);

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	

	@RequestMapping("/download/manager")
	public void downloadmanager(String startDay, String endDay,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Department store = admin.getDepartment();
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("department", store));
			List<HyAdmin> admins = hyAdminService.findList(null,filters,null);
			List<ManagerProfit> managerProfits = new ArrayList<>();
			BigDecimal zongincome = new BigDecimal("0.0");
			BigDecimal zongoutcome = new BigDecimal("0.0");
			BigDecimal zongprofit = new BigDecimal("0.0");
			BigDecimal zongprofitrate = new BigDecimal("0.0");

			/** 遍历当前页员工数据，返回前端需要的数据格式 */
			for (HyAdmin employee : admins) {
				String name = employee.getUsername();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("username", employee.getName());
				ManagerProfit managerProfit = new ManagerProfit();
				managerProfit.setUsername(employee.getName());
				
				//实收款
				
				String jpql = "select sum(r.money) from hy_order as o,hy_receipt_refund as r "
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+"'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
						+"' and o.id = r.order_id and r.type = 0  and o.type = 1 and o.status = 3 and o.creator_id = '"+name+"'";
	             
				System.out.println(jpql);
				List<Object[]> list = hyOrderService.statis(jpql);
				BigDecimal income = new BigDecimal(0);
				if (!list.isEmpty() &&list.size()!=0&& list.get(0)!=null) {
					Object iObject = list.get(0);
					income = new BigDecimal(iObject.toString());
//					BigInteger income = new BigInteger(iObject.toString());
					map.put("income", income);
				} else {
					map.put("income", 0);
				}	
				
//				//返利
//				String fanli = "select sum(b.amount) from hy_branch_pre_save as b,hy_order as o "
//						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//						+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//						+ "' and o.id=b.order_id and b.type=5 and o.creator_id = '"+name+"'";
//				List<Object[]> fanlilist = hyOrderService.statis(fanli);
//				BigDecimal incomefanli = new BigDecimal(0);
//				if (!fanlilist.isEmpty() &&fanlilist.size()!=0&& fanlilist.get(0)!=null) {
//					Object iObject = list.get(0);
//					incomefanli = new BigDecimal(iObject.toString());
//				} 
//				income = income.add(incomefanli);
				map.put("income", income);
						
				managerProfit.setIncome(income);
				zongincome = zongincome.add(income);
				//实退款
				String jpql2 = "select sum(r.money) from hy_order as o,hy_receipt_refund as r "
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+"'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
						+"' and o.id = r.order_id and r.type = 1 and o.type = 1 and o.status = 3 and o.creator_id = '"+name+"'";
	             
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
						+"' and o.id = a.order_id and o.type = 1 and o.status = 3 and o.creator_id = '"+name+"'";
				
				System.out.println(jpql3);
				List<Object[]> list3 = hyOrderService.statis(jpql3);
				BigDecimal refund2 = new BigDecimal(0);
				if (!list3.isEmpty() &&list3.size()!=0&& list3.get(0)!=null) {
					Object iObject = list3.get(0);
					refund2 = new BigDecimal(iObject.toString());
				}
				
				//保险
				String jpql4 = "select sum(o.jiusuan_money  - ifnull(o.discounted_price,0) - ifnull(o.store_fan_li,0) + o.tip) from hy_order as o " 
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay 
						+ "' and o.status = 3 and o.type = 1 and  o.creator_id = '"+name+"'";
				
				
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
				managerProfit.setOutcome(outcome);
				zongoutcome = zongoutcome.add(outcome);
				
				BigDecimal profit = new BigDecimal(0);
				BigDecimal profitRate = new BigDecimal(0);
				BigDecimal zero = new BigDecimal("0.0");
				profit = income.subtract(outcome);
				if(income.compareTo(zero)!=0){
//					profit = income.subtract(outcome);
					profitRate = profit.divide(income,3,BigDecimal.ROUND_HALF_UP);
				}
				map.put("profit",profit);
				map.put("profitRate",profitRate);
				managerProfit.setProfit(profit);
				managerProfit.setProfitRate(profitRate);
				zongprofit = zongprofit.add(profit);
				zongprofitrate = zongprofitrate.add(profitRate);
				
				result.add(map);
				managerProfits.add(managerProfit);		
			}
			ManagerProfit managerProfit = new ManagerProfit();
			managerProfit.setUsername("总计");
			managerProfit.setIncome(zongincome);
			managerProfit.setOutcome(zongoutcome);
			managerProfit.setProfit(zongprofit);
			BigDecimal zero = new BigDecimal("0.0");
			if(zongincome.compareTo(zero)!=0){
				zongprofitrate = zongprofit.divide(zongincome);
			}
			managerProfit.setProfitRate(zongprofitrate);
			managerProfits.add(managerProfit);
			
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("利润统计表（经理）");
			String fileName = "经理利润统计表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "managerProfitStatistics.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, managerProfits, fileName, tableTitle, configFile);
			

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	

	public class ManagerProfit{
		String username;
		BigDecimal income;
		BigDecimal outcome;
		BigDecimal profit;
		BigDecimal profitRate;
		
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
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
	
	
}
