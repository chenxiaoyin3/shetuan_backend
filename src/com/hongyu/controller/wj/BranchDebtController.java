package com.hongyu.controller.wj;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BranchReceiptServicer;
import com.hongyu.entity.BranchReceiptTotalServicer;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.PayServicer;
import com.hongyu.entity.PayablesBranchsettle;
import com.hongyu.entity.ReceiptServicer;
import com.hongyu.entity.ReceiptTotalServicer;
import com.hongyu.service.BranchReceiptServicerService;
import com.hongyu.service.BranchReceiptTotalServicerService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.PayablesBranchsettleService;
import com.hongyu.util.liyang.EmployeeUtil;

/**
 * 分工公司欠款冲抵记录
 * @author wj
 *
 */

@Controller
@RequestMapping("/admin/branchQK/")
public class BranchDebtController {
	
	@Autowired
    BranchReceiptServicerService branchReceiptServicerService;
	
	@Autowired
	HyOrderService hyOrderService;
	
	@Autowired
	HyAdminService hyAdminService;
	
	@Autowired
	HyCompanyService hyCompanyService;
	
	@Autowired
	PayablesBranchsettleService payablesBranchsettleService;
	
	@Autowired
	BranchReceiptTotalServicerService branchReceiptTotalServicerService;
	
	/**
	 * 分公司欠款余额
	 * @param session
	 * @return
	 */
	@RequestMapping("balance/view")
	@ResponseBody
	public Json getBalance(HttpSession session){
		Json json=new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		
		try {
			List<Filter> filters = new ArrayList<>();
			HyAdmin admin = hyAdminService.find(username);
			Department department = EmployeeUtil.getCompany(admin);
			HyCompany company = department.getHyCompany();
			
			
			filters.clear();
			filters.add(Filter.eq("companyId",company.getID()));
			List<BranchReceiptTotalServicer> branchReceiptTotalServicers = branchReceiptTotalServicerService.findList(null,filters,null);
			BigDecimal balance = BigDecimal.ZERO;
			if(branchReceiptTotalServicers.size()!=0){
				balance = balance.add(branchReceiptTotalServicers.get(0).getBalance());
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(balance);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			e.printStackTrace();
			json.setMsg("查询失败");
		}
		return json;
	}
	
	
	/**
	 * 充值列表
	 * @param pageable
	 * @param startDate
	 * @param endDate
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "qkcz/list")
	@ResponseBody
	public Json qkcz(Pageable pageable,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,HttpSession session){
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		List<Filter> filters = new ArrayList<>();
		
		try{
			Department department = EmployeeUtil.getCompany(hyAdminService.find(username));
//			List<Filter> filters2 = new ArrayList<>();
//			filters2.add(Filter.eq("hyDepartment", department));
//			List<HyCompany> companies = hyCompanyService.findList(null,filters2,null); 
			HyCompany company = department.getHyCompany();
			
//			filters.clear();
			if(startDate!=null){
				filters.add(Filter.ge("date", startDate));
			}
			if(endDate!=null){
				filters.add(Filter.le("date", endDate));
			}
			filters.add(Filter.eq("companyId",company.getID()));
			filters.add(Filter.eq("state", 0));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("date"));
			pageable.setOrders(orders);
			Page<BranchReceiptServicer> page=branchReceiptServicerService.findPage(pageable);
			List<Map<String, Object>> result=new LinkedList<>();
			for(BranchReceiptServicer branchReceiptServicer:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				map.put("operator",branchReceiptServicer.getOperator());
				map.put("amount", branchReceiptServicer.getAmount());
				map.put("balance", branchReceiptServicer.getBalance());
				map.put("date", branchReceiptServicer.getDate());
				HyOrder hyOrder = hyOrderService.find(branchReceiptServicer.getOrderOrSettleId());
				map.put("orderNumber",hyOrder.getOrderNumber());
				map.put("orderName",hyOrder.getName());
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
		try{
			Department department = EmployeeUtil.getCompany(hyAdminService.find(username));
//			List<Filter> filters2 = new ArrayList<>();
//			filters2.add(Filter.eq("hyDepartment", department));
//			List<HyCompany> companies = hyCompanyService.findList(null,filters2,null); 
			HyCompany company = department.getHyCompany();
//			filters.clear();
			if(startDate!=null){
				filters.add(Filter.ge("date", startDate));
			}
			if(endDate!=null){
				filters.add(Filter.le("date", endDate));
			}
			filters.add(Filter.eq("companyId",company.getID()));
			filters.add(Filter.eq("state", 1));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("date"));
			pageable.setOrders(orders);
			Page<BranchReceiptServicer> page=branchReceiptServicerService.findPage(pageable);
			List<Map<String, Object>> result=new LinkedList<>();
			for(BranchReceiptServicer branchReceiptServicer:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				map.put("operator",branchReceiptServicer.getOperator());
				map.put("amount", branchReceiptServicer.getAmount());
				map.put("balance", branchReceiptServicer.getBalance());
				map.put("date", branchReceiptServicer.getDate());
				PayablesBranchsettle payablesBranchsettle = payablesBranchsettleService.find(branchReceiptServicer.getOrderOrSettleId());
//				map.put("payer",payServicer.getPayer());
				map.put("payNumber", payablesBranchsettle.getPayNumber());
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
	
}
