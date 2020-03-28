package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.hongyu.entity.BankList;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Provider;
import com.hongyu.entity.ProviderBalance;
import com.hongyu.service.BankListService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.ProviderBalanceService;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping({"admin/business/providerbalance"})
public class ProviderBalanceController {
	
	  @Resource(name="providerBalanceServiceImpl")
	  private ProviderBalanceService providerBalanceService;
	  
	  @Resource(name="hyAdminServiceImpl")
	  private HyAdminService hyAdminService;
	  
	  @Resource(name = "bankListServiceImpl")
	  private BankListService  bankListService;
	
	  @RequestMapping(value="/paypage/view")
	  @ResponseBody
	  public Json providerBalancePay(@DateTimeFormat(iso=ISO.DATE)Date startdate,@DateTimeFormat(iso=ISO.DATE)Date enddate, Boolean state, String name, Pageable pageable, HttpServletRequest request, HttpSession session)
	  {
		
		Json j = new Json();
		Date start = null;
		Date end = null;
		List<Filter> filters = new ArrayList<>();
		
		try {
			if (startdate != null) {
				start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("createTime", start));
			}
			if (enddate != null) {
				end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("createTime", end));
			}
			if (state != null) {
				filters.add(Filter.eq("state", state));
			}
			if (name != null) {
				filters.add(Filter.like("name", name));
			}
			//需要审核后的才能显示在付款列表
			filters.add(Filter.eq("isAudited", Boolean.TRUE));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			pageable.setFilters(filters);
			Page<ProviderBalance> page = providerBalanceService.findPage(pageable);
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(page);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		
		
		return j;
	  }
	  
	  
	  @RequestMapping(value="/pay")
	  @ResponseBody
	  public Json providerAudit(ProviderBalance balance, HttpServletRequest request, HttpSession session)
	  {
		
		Json j = new Json();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username); //获取当前用户
		
		try {
			ProviderBalance oldbalance = providerBalanceService.find(balance.getId());
			if (oldbalance == null) {
				j.setSuccess(false);
				j.setMsg("付款失败，不存在指定的供货商结算单");
				j.setObj(null);
				return j;
			}
			if (oldbalance.getState()) {
				j.setSuccess(false);
				j.setMsg("供货商结算单已经完成付款");
				j.setObj(null);
				return j;
			}
			oldbalance.setPayerAccount(balance.getPayerAccount());
			oldbalance.setPayerBank(balance.getPayerBank());
			oldbalance.setPayerName(balance.getPayerName());
			oldbalance.setBalanceTime(new Date());
			oldbalance.setOperator(admin);
			oldbalance.setState(true);
			providerBalanceService.update(oldbalance);
			j.setSuccess(true);
			j.setMsg("付款成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("付款失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		return j;
	  }
	  
	  
	  @RequestMapping(value="/auditpage/view")
	  @ResponseBody
	  public Json providerBalanceAudit(@DateTimeFormat(iso=ISO.DATE)Date startdate,@DateTimeFormat(iso=ISO.DATE)Date enddate, Boolean isAudited, String name, Pageable pageable, HttpServletRequest request, HttpSession session)
	  {
		
		Json j = new Json();
		Date start = null;
		Date end = null;
		List<Filter> filters = new ArrayList<>();
		
		try {
			if (startdate != null) {
				start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("createTime", start));
			}
			if (enddate != null) {
				end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("createTime", end));
			}
			if (isAudited != null) {
				filters.add(Filter.eq("isAudited", isAudited));
			}
			if (name != null) {
				filters.add(Filter.like("name", name));
			}
			//需要审核后的才能显示在付款列表
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			pageable.setFilters(filters);
			Page<ProviderBalance> page = providerBalanceService.findPage(pageable);
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(page);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		
		
		return j;
	  }
	  
	  @RequestMapping(value="/audit")
	  @ResponseBody
	  public Json providerAudit(Long id, HttpServletRequest request, HttpSession session)
	  {
		
		Json j = new Json();
		
		try {
			ProviderBalance balance = providerBalanceService.find(id);
			if (balance == null) {
				j.setSuccess(false);
				j.setMsg("审核失败，不存在指定的供货商结算单");
				j.setObj(null);
				return j;
			}
			if (balance.getIsAudited()) {
				j.setSuccess(false);
				j.setMsg("供货商结算单已经完成审核");
				j.setObj(null);
				return j;
			}
			balance.setIsAudited(true);
			providerBalanceService.update(balance);
			j.setSuccess(true);
			j.setMsg("审核成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("审核失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		return j;
	  }
	  
	//采购部财务查询采购单付款记录
			@RequestMapping(value="/banklist/view")
			@ResponseBody
			public Json purchaseFinancerBankList(HttpSession session) {
				Json j = new Json();
				try {
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("type", BankList.BankType.businessBank));
					List<BankList> banklist = bankListService.findList(null, filters, null);
					List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
					for (BankList list : banklist) {
						Map<String, Object> m = new HashMap<String, Object>();
						m.put("payerName", list.getAccountName());
						m.put("bankName", list.getBankName());
						m.put("bankCode", list.getBankCode());
						m.put("bankAccount", list.getBankAccount());
						lists.add(m);
					}
					j.setSuccess(true);
					j.setMsg("查询成功");
					j.setObj(lists);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					j.setSuccess(false);
					j.setMsg("查询失败");
					j.setObj(e);
				}
				
				
				
				return j;
			}

}
