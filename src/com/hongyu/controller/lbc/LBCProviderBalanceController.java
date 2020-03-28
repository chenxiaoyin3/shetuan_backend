package com.hongyu.controller.lbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.AdvancePurchaseBalanceProvider;
import com.hongyu.entity.ProviderBalance;
import com.hongyu.service.AdvancePurchaseBalanceProviderService;
import com.hongyu.service.ProviderBalanceService;

@Controller
@RequestMapping("/admin/providerBalance/")
public class LBCProviderBalanceController {
	
	@Resource(name = "providerBalanceServiceImpl")
	ProviderBalanceService providerBalanceService;
	
	@Resource(name = "advancePurchaseBalanceProviderServiceImpl")
	AdvancePurchaseBalanceProviderService advancePurchaseBalanceProviderService;
	
	@RequestMapping(value="/list/view")
	@ResponseBody
	public Json ProviderBalanceList(Pageable pageable,HttpSession session, Date start, Date end, Boolean status, String providerName){
		Json json = new Json();
		
		try {
			List<Filter> filters = new ArrayList<Filter>();
			if(start != null) {
				filters.add(Filter.ge("createTime", start));
			}
			if(end != null) {
				filters.add(Filter.le("createTime", end));
			}
			if(status != null) {
				filters.add(Filter.eq("state", status));
			}
			if(providerName != null) {
				filters.add(Filter.like("name", providerName));
			}
			
			
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);;
			Page<ProviderBalance> page = providerBalanceService.findPage(pageable);
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
			
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		
		
		return json;
	}
	
	@RequestMapping(value="/detail")
	@ResponseBody
	public Json ProviderBalanceDetail(Pageable pageable,HttpSession session,Long providerBalance_id){
		Json json = new Json();
		try {
			ProviderBalance providerBalance = providerBalanceService.find(providerBalance_id);
			json.setSuccess(true);
			json.setMsg("查询成功");
			Map<String, Object> map = new HashMap<String,Object>();
			
			json.setObj(providerBalance);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		return json;
	}
	
	
	@RequestMapping(value="/changeStatus")
	@ResponseBody
	public Json ProviderBalanceStatusChange(Pageable pageable,HttpSession session,Long providerBalance_id){
		Json json = new Json();
		try {
			ProviderBalance providerBalance = providerBalanceService.find(providerBalance_id);
			providerBalance.setState(!providerBalance.getState());
			providerBalance.setBalanceTime(new Date());
			providerBalanceService.update(providerBalance);
			
			AdvancePurchaseBalanceProvider advancePurchaseBalanceProvider = new AdvancePurchaseBalanceProvider();
			advancePurchaseBalanceProvider.setPayeeName(providerBalance.getPayeeName());
			advancePurchaseBalanceProvider.setPayeeAccount(providerBalance.getPayeeAccount());
			advancePurchaseBalanceProvider.setPayerName(providerBalance.getPayerName());
			advancePurchaseBalanceProvider.setPayerAccount(providerBalance.getPayerAccount());
			advancePurchaseBalanceProvider.setOperator(providerBalance.getPayerName());
			advancePurchaseBalanceProvider.setAdvanceAmount(providerBalance.getBalanceMoney().doubleValue());
			advancePurchaseBalanceProvider.setPayTime(providerBalance.getBalanceTime());
			advancePurchaseBalanceProviderService.save(advancePurchaseBalanceProvider);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		return json;
	}
	
	
}
