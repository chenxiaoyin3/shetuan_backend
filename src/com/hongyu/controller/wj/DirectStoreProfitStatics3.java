package com.hongyu.controller.wj;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import com.hongyu.Json;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.StoreService;
import com.hongyu.util.liyang.EmployeeUtil;

/**
 * 直营门店利润统计
 * 分公司可看
 */

@Controller
@RequestMapping("/admin/directstore_branch")
public class DirectStoreProfitStatics3 {
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
	

	@RequestMapping("/branch")
	@ResponseBody
	public Json branch(String startDay, String endDay,HttpSession session,HttpServletRequest request){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Long branchId = EmployeeUtil.getCompany(hyAdmin).getId();
			
			String jpql = "select id from hy_department where model='分公司连锁发展' and parent="+branchId;
             
			System.out.println(jpql);
			Long departmentId = (long) 0;
			List<Object[]> l1 = departmentService.statis(jpql);
			if (l1.size() != 0 && l1!=null) {
				Object iObject = l1.get(0);
				departmentId = new BigInteger(iObject.toString()).longValue();
			}else{
				json.setMsg("该分公司没有连锁发展");
				json.setSuccess(true);
				return json;
			}
			
			//拿到当前分公司下连锁发展的所有门店
			jpql = "select id from hy_store where type=2 and suoshudepartment_id= "
				+departmentId;
			System.out.println(jpql);
			List<Object[]> l2 = departmentService.statis(jpql);
			
			List<HashMap<String, Object>> res = new ArrayList<>();
//			for(Object[] objects :l2){
			for(int i=0;i<l2.size();i++){
				Object objects = l2.get(i); 
				Long storeId = Long.parseLong(objects.toString());
				HashMap<String,Object> map = new HashMap<>();
				Store store = storeService.find(storeId);
				map.put("storeName", store.getStoreName());
				
				//实收款 
				String s1 = "select sum(r.money) from hy_order as o,hy_receipt_refund as r where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
				 			+"' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
				 			+"' and o.id = r.order_id and r.type = 0 and o.type = 1 and o.status = 3 and o.store_id ="+storeId;
				
				List<Object[]> list = hyOrderService.statis(s1);
				BigDecimal income = new BigDecimal(0);
				if (!list.isEmpty() &&list.size()!=0&& list.get(0)!=null) {
					Object iObject = list.get(0);
					income = new BigDecimal(iObject.toString());
				}
				
//				//返利
//				String fanli = "select sum(b.amount) from hy_branch_pre_save as b,hy_order as o "
//						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//						+ "'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//						+ "' o.id=b.order_id and b.type=5 and o.store_id ="+storeId;
//				List<Object[]> fanlilist = hyOrderService.statis(fanli);
//				BigDecimal incomefanli = new BigDecimal(0);
//				if (!fanlilist.isEmpty() &&fanlilist.size()!=0&& fanlilist.get(0)!=null) {
//					Object iObject = list.get(0);
//					incomefanli = new BigDecimal(iObject.toString());
//				} 
//				map.put("income", income.add(incomefanli));
				map.put("income", income);
				
				//实退款 (需确认是否待财务确认过再统计）
				String s2 = "select sum(r.money) from hy_order as o,hy_receipt_refund as r "
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+"'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
						+"' and o.id = r.order_id and r.type = 1 and o.type = 1 and o.status = 3 and o.store_id = "+storeId;
				List<Object[]> list2 = hyOrderService.statis(s2);
				BigDecimal refund = new BigDecimal(0);
				if (!list2.isEmpty() &&list2.size()!=0&& list2.get(0)!=null) {
					Object iObject = list2.get(0);
					refund = new BigDecimal(iObject.toString());
				}
				
				//增值业务供应商付款 
				String s3 = "select sum(a.money) from hy_order as o , hy_added_service as a "
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+"' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
						+"' and o.id = a.order_id and o.type = 1 and o.status = 3 and o.store_id = "+storeId;
				System.out.println(s3);
				List<Object[]> list3 = hyOrderService.statis(s3);
				
				BigDecimal refund2 = new BigDecimal(0);
				if (!list3.isEmpty() &&list3.size()!=0&& list3.get(0)!=null) {
					Object iObject = list3.get(0);
					refund2 = new BigDecimal(iObject.toString());
				}
				
				//保险
//				String s4 = "select sum(i.shifu_money) from hy_order as o , hy_insurance_order as i "
//						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//						+"' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//						+"' and o.id=i.order_id and o.store_id = "+storeId;
				
//				String s4 = "select sum(c.settlement_price) from hy_order o , hy_order_customer c, hy_order_item i "
//						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//						+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//						+ "' and i.order_id=o.id and c.item_id = i.id and c.is_insurance=1"
//						+ " and o.store_id = "+storeId;
				String s4 = "select sum(o.jiusuan_money  - ifnull(o.discounted_price,0) - ifnull(o.store_fan_li,0) + o.tip) from hy_order as o " 
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay 
						+ "' and o.status = 3 and o.type = 1 and  o.store_id = "+storeId;
				
				
				List<Object[]> list4 = hyOrderService.statis(s4);
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
				res.add(map);
			}
			json.setObj(res);
			json.setMsg("操作成功");
			json.setSuccess(true);		
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
		}
		
		return json;
		
	}
	

	@RequestMapping("/download/branch")
	public void downloadbranch(String startDay, String endDay,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Long branchId = EmployeeUtil.getCompany(hyAdmin).getId();
			
			String jpql = "select id from hy_department where model='分公司连锁发展' and parent="+branchId;
             
			System.out.println(jpql);
			Long departmentId = (long) 0;
			List<Object[]> l1 = departmentService.statis(jpql);
			if (l1.size() != 0 && l1!=null) {
				Object iObject = l1.get(0);
				departmentId = new BigInteger(iObject.toString()).longValue();
			}else{
				return ;
			}
			
			//拿到当前分公司下连锁发展的所有门店
			jpql = "select id from hy_store where type=2 and suoshudepartment_id= "
				+departmentId;
			System.out.println(jpql);
			List<Object[]> l2 = departmentService.statis(jpql);
			
			List<HashMap<String, Object>> res = new ArrayList<>();
			List<BranchProfit> branchProfits = new ArrayList<>();
//			for(Object[] objects :l2){
			
			BigDecimal zongincome = new BigDecimal("0.0");
			BigDecimal zongoutcome = new BigDecimal("0.0");
			BigDecimal zongprofit = new BigDecimal("0.0");
			BigDecimal zongprofitrate = new BigDecimal("0.0");
			
			
			for(int i=0;i<l2.size();i++){
				BranchProfit branchProfit = new BranchProfit();
				Object objects = l2.get(i); 
				Long storeId = Long.parseLong(objects.toString());
				HashMap<String,Object> map = new HashMap<>();
				Store store = storeService.find(storeId);
				map.put("storeName", store.getStoreName());
				branchProfit.setStoreName(store.getStoreName());
				//实收款 
				String s1 = "select sum(r.money) from hy_order as o,hy_receipt_refund as r where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
				 			+"' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
				 			+"' and o.id = r.order_id and r.type = 0 and o.type = 1 and o.status = 3 and o.store_id ="+storeId;
				
				List<Object[]> list = hyOrderService.statis(s1);
				BigDecimal income = new BigDecimal(0);
				if (!list.isEmpty() &&list.size()!=0&& list.get(0)!=null) {
					Object iObject = list.get(0);
					income = new BigDecimal(iObject.toString());
//					BigInteger income = new BigInteger(iObject.toString());
				} 
				
//				//返利
//				String fanli = "select sum(b.amount) from hy_branch_pre_save as b,hy_order as o "
//						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//						+ "'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//						+ "' o.id=b.order_id and b.type=5 and o.store_id ="+storeId;
//				List<Object[]> fanlilist = hyOrderService.statis(fanli);
//				BigDecimal incomefanli = new BigDecimal(0);
//				if (!fanlilist.isEmpty() &&fanlilist.size()!=0&& fanlilist.get(0)!=null) {
//					Object iObject = list.get(0);
//					incomefanli = new BigDecimal(iObject.toString());
//				} 
//				income = income.add(incomefanli);
				map.put("income", income);
				
				branchProfit.setIncome(income);
				zongincome = zongincome.add(income);
				//实退款
				String s2 = "select sum(r.money) from hy_order as o,hy_receipt_refund as r "
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+"'and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
						+"' and o.id = r.order_id and r.type = 1 and o.type = 1 and o.status = 3 and o.store_id = '"+storeId;
				List<Object[]> list2 = hyOrderService.statis(s2);
				BigDecimal refund = new BigDecimal(0);
				if (!list2.isEmpty() &&list2.size()!=0&& list2.get(0)!=null) {
					Object iObject = list2.get(0);
					refund = new BigDecimal(iObject.toString());
				}
				
				//增值业务供应商付款 
				String s3 = "select sum(a.money) from hy_order as o , hy_added_service as a "
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+"' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
						+"' and o.id = a.order_id and o.status = 3 and o.type = 1 and o.store_id = "+storeId;
				System.out.println(s3);
				List<Object[]> list3 = hyOrderService.statis(s3);
				
				BigDecimal refund2 = new BigDecimal(0);
				if (!list3.isEmpty() &&list3.size()!=0&& list3.get(0)!=null) {
					Object iObject = list3.get(0);
					refund2 = new BigDecimal(iObject.toString());
				}
				
				//保险
//				String s4 = "select sum(c.settlement_price) from hy_order o , hy_order_customer c, hy_order_item i "
//						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
//						+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay
//						+ "' and i.order_id=o.id and c.item_id = i.id and c.is_insurance=1"
//						+ " and o.store_id = "+storeId;
				String s4 = "select sum(o.jiusuan_money  - ifnull(o.discounted_price,0) - ifnull(o.store_fan_li,0) + o.tip) from hy_order as o " 
						+ "where DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)>='"+startDay
						+ "' and DATE_SUB(o.fatuandate,INTERVAL -(o.tianshu-1) DAY)<='"+endDay 
						+ "' and o.status = 3 and o.type = 1 and  o.store_id = "+storeId;
				
				List<Object[]> list4 = hyOrderService.statis(s4);
				BigDecimal refund3 = new BigDecimal(0);
				if (!list4.isEmpty() &&list4.size()!=0&& list4.get(0)!=null) {
					Object iObject = list4.get(0);
					refund3 = new BigDecimal(iObject.toString());
				}
				//支出
				BigDecimal outcome = new BigDecimal(0);
				outcome = outcome.add(refund).add(refund2).add(refund3);
				map.put("outcome", outcome);
				branchProfit.setOutcome(outcome);
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
				branchProfit.setProfit(profit);
				branchProfit.setProfitRate(profitRate);
				zongprofit = zongprofit.add(profit);
				zongprofitrate = zongprofitrate.add(profitRate);
				branchProfits.add(branchProfit);
				res.add(map);
			}
			
			BranchProfit branchProfit = new BranchProfit();
			branchProfit.setStoreName("总计");
			branchProfit.setIncome(zongincome);
			branchProfit.setOutcome(zongoutcome);
			branchProfit.setProfit(zongprofit);
			branchProfit.setProfitRate(zongprofitrate);
			branchProfits.add(branchProfit);

			
			
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("直营门店利润报表");
			String fileName = "直营门店利润报表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "storeProfitStatistics.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, branchProfits, fileName, tableTitle, configFile);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	public class BranchProfit{
		String storeName;
		BigDecimal income;
		BigDecimal outcome;
		BigDecimal profit;
		BigDecimal profitRate;
	
		public String getStoreName() {
			return storeName;
		}
		public void setStoreName(String storeName) {
			this.storeName = storeName;
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
