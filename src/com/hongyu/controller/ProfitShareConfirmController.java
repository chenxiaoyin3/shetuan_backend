package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.PayShareProfit;
import com.hongyu.entity.ProfitShareConfirm;
import com.hongyu.entity.ProfitShareConfirmDetail;
import com.hongyu.entity.ProviderBalance;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.PayShareProfitService;
import com.hongyu.service.ProfitShareConfirmService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/profitshareconfirm")
public class ProfitShareConfirmController {
	
	@Resource(name="profitShareConfirmServiceImpl")
	ProfitShareConfirmService profitShareConfirmService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "payShareProfitServiceImpl")
	PayShareProfitService payShareProfitService;
	
	@Resource(name="hyAdminServiceImpl")
	  HyAdminService hyAdminService;
	
	@RequestMapping(value="/page/view")
	  @ResponseBody
	  public Json profitShareConfirmPage(@DateTimeFormat(pattern="yyyy-MM-dd")Date startdate,@DateTimeFormat(pattern="yyyy-MM-dd")Date enddate, Integer status, Pageable pageable, String branchName, HttpServletRequest request, HttpSession session)
	  {
		
		Json j = new Json();
		
		String username = (String)session.getAttribute("principal");
	    HyAdmin admin = (HyAdmin)this.hyAdminService.find(username);
	    
	    Department department = admin.getDepartment();
	    while (!department.getIsCompany()) {
	    	department = department.getHyDepartment();
	    }
	    
	    
		Date start = null;
		Date end = null;
		List<Filter> filters = new ArrayList<>();
		
		try {
			if (startdate != null) {
				start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("generateDate", start));
			}
			if (enddate != null) {
				end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("generateDate", end));
			}
			if (status != null) {
				filters.add(Filter.eq("state", status));
			}
			
			if (StringUtils.isNotBlank(branchName)) {
				filters.add(Filter.like("branchName", branchName));
			}
			
			// 分公司只能看自己的
			if (department.getTreePath().contains(",1,")) {
				filters.add(Filter.eq("branch", department));
			}
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			pageable.setFilters(filters);
			
			Page<ProfitShareConfirm> page = profitShareConfirmService.findPage(pageable);
			List<Map<String, Object>> list = new ArrayList<>();
			for (ProfitShareConfirm confirm : page.getRows()) {
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("confirmCode", confirm.getConfirmNum()));
				List<PayShareProfit> payShareProfits = payShareProfitService.findList(null,filters2,null);
				Map<String, Object> map = new HashMap<>();
				map.put("id", confirm.getId());
				map.put("branchName", confirm.getBranchName());
				map.put("billingCycleStart", confirm.getBillingCycleStart());
				map.put("billingCycleEnd", confirm.getBillingCycleEnd());
				map.put("amount", confirm.getAmount());
				map.put("generateDate", confirm.getGenerateDate());
				map.put("status", confirm.getState());
				if(payShareProfits.size()==0){
					map.put("payShareProfitId", null);
				}else{
					map.put("payShareProfitId", payShareProfits.get(0).getId());
				}
				
				list.add(map);
			}
			Page<Map<String, Object>> result = new Page<>(list, page.getTotal(), pageable);
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(result);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		
		
		return j;
	  }
	
	
	@RequestMapping(value="/detail/view")
	  @ResponseBody
	  public Json profitShareConfirmDetail(Long id, HttpServletRequest request, HttpSession session)
	  {
		
		Json j = new Json();
		
		try {
			ProfitShareConfirm confirm = profitShareConfirmService.find(id);
			if (confirm == null) {
				j.setSuccess(false);
				j.setMsg("分成确认单不存在");
				j.setObj(null);
				return j;
			}
			
			Map<String, Object> result = new HashMap<>();
			result.put("id", confirm.getId());
			result.put("branchName", confirm.getBranchName());
			result.put("accountName", confirm.getBankList().getAccountName());
			result.put("bankName", confirm.getBankList().getBankName());
			result.put("bankAccount", confirm.getBankList().getBankAccount());
			result.put("bankCode", confirm.getBankList().getBankCode());
			result.put("billingCycleStart", confirm.getBillingCycleStart());
			result.put("billingCycleEnd", confirm.getBillingCycleEnd());
			result.put("amount", confirm.getAmount());
			result.put("status", confirm.getState());
			
			BigDecimal profitAmount = new BigDecimal(0.00);
			BigDecimal refundAmount = new BigDecimal(0.00);
			List<Map<String, Object>> details = new ArrayList<>();
			List<Map<String, Object>> refundDetails = new ArrayList<>();
			for (ProfitShareConfirmDetail detail : confirm.getDetails()) {
				Map<String, Object> map = new HashMap<>();
				map.put("detailId", detail.getId());
				map.put("orderCode", detail.getOrderCode());
				map.put("productCode", detail.getProductId());
				map.put("productName", detail.getProductName());
				HyGroup group = hyGroupService.find(detail.getOrder().getGroupId());
				map.put("returnDate", group.getEndDay());
				map.put("amount", detail.getAmount());
				map.put("shareProfit", detail.getShareProfit());
				map.put("percentBranch", detail.getPercentBranch());
				if (detail.getIsIncome()) {
					details.add(map);
					profitAmount = profitAmount.add(detail.getShareProfit());
				} else {
					refundDetails.add(map);
					refundAmount = refundAmount.add(detail.getShareProfit());
				}
				
			}
			
			result.put("details", details);
			result.put("refundDetails", refundDetails);
			result.put("profitAmount", profitAmount);
			result.put("refundAmount", refundAmount);
			result.put("dismissReason", confirm.getDismissReason());
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(result);
		
		
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		return j;
	  }
	
	@RequestMapping(value="/confirm")
	  @ResponseBody
	  public Json profitShareConfirmRefuse(Long id, Boolean isApproved, String refuseReason, HttpServletRequest request, HttpSession session)
	  {
		
		Json j = new Json();

		
		try {
			ProfitShareConfirm confirm = profitShareConfirmService.find(id);
			if (confirm == null) {
				j.setSuccess(false);
				j.setMsg("分成确认单不存在");
				j.setObj(null);
				return j;
			}
			
			if (!isApproved) {
				confirm.setDismissReason(refuseReason);
				confirm.setConfirmDate(new Date());
				confirm.setState(Constants.PROFIT_SHARE_CONFIRM_STATUS_RETURNED);
				profitShareConfirmService.update(confirm);
			} else {
				
				
				/** 在这里添加分公司分成付款记录 **/
				PayShareProfit payShareProfit = new PayShareProfit();
				payShareProfit.setHasPaid(0);//待付款
				payShareProfit.setType(1);//分公司分成
				payShareProfit.setClient(confirm.getBranchName());
				payShareProfit.setBillingCycleStart(confirm.getBillingCycleStart());
				payShareProfit.setBillingCycleEnd(confirm.getBillingCycleEnd());
				payShareProfit.setAmount(confirm.getAmount());
				payShareProfit.setRemark(confirm.getRemark());
				payShareProfit.setBankListId(confirm.getBankList().getId());
				payShareProfit.setConfirmCode(confirm.getConfirmNum());
				payShareProfitService.save(payShareProfit);
				
				confirm.setConfirmDate(new Date());
				confirm.setState(Constants.PROFIT_SHARE_CONFIRM_STATUS_CONFIRMED);
				profitShareConfirmService.update(confirm);
			}
			
			j.setSuccess(true);
			j.setMsg("设置成功");
			j.setObj(null);
		
		
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("设置失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		return j;
	  }
	
	@RequestMapping(value="/modify")
	  @ResponseBody
	  public Json profitShareConfirmModify(Long id, String remark, BigDecimal modifyAmount, HttpServletRequest request, HttpSession session)
	  {
		
		Json j = new Json();

		
		try {
			ProfitShareConfirm confirm = profitShareConfirmService.find(id);
			if (confirm == null) {
				j.setSuccess(false);
				j.setMsg("分成确认单不存在");
				j.setObj(null);
				return j;
			}
			
			confirm.setIsModified(true);
			confirm.setRemark(remark);
			confirm.setModifyAmount(modifyAmount);
			confirm.setAmount(confirm.getAmount().add(modifyAmount));
			confirm.setState(Constants.PROFIT_SHARE_CONFIRM_STATUS_WAIT_FOR_CONFIRMATION);
			profitShareConfirmService.update(confirm);
			j.setSuccess(true);
			j.setMsg("设置成功");
			j.setObj(null);
		
		
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("设置失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		return j;
	  }
	
	
	@RequestMapping(value="/test")
	  @ResponseBody
	  public Json profitShareConfirmTest() {
		Json j = new Json();
		try {
			profitShareConfirmService.calculateProfitshareConfirmCurMonth();
			j.setMsg("生成成功");
			j.setSuccess(true);
			j.setObj(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			j.setMsg("生成失败");
			j.setSuccess(false);
			j.setObj(null);
		}
		return j;
		
	}
	
	
	
	
	
}
