package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.StoreLineOrderController.ReceiptRefund;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyReceiptRefundService;

@Controller
@RequestMapping("admin/branch_finance/")
public class BranchFinanceController {
	
	@Resource(name = "hyReceiptRefundServiceImpl")
	HyReceiptRefundService hyReceiptRefundService;
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	
	@RequestMapping("receipt_refund/page/view")
	@ResponseBody
	public Json receiptRefundList(Pageable pageable,Integer status,Integer type,HttpSession session) {
		Json json = new Json();
		try {
			String username = (String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			//查找该财务所属分公司
			Department branch = departmentService.findCompanyOfDepartment(hyAdmin.getDepartment());
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("branch", branch));
			if(status!=null) {
				filters.add(Filter.eq("status", status));
			}
			if(type!=null) {
				filters.add(Filter.eq("type", type));
			}
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			
			Page<HyReceiptRefund> hyReceiptRefunds = hyReceiptRefundService.findPage(pageable);
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hyReceiptRefunds);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	@RequestMapping("receipt_refund/modify/")
	@ResponseBody
	public Json receiptRefundModify(Long id,BigDecimal adjustMoney,String reason) {
		Json json = new Json();
		try {
			HyReceiptRefund receiptRefund = hyReceiptRefundService.find(id);
			if(receiptRefund==null) {
				throw new Exception("该记录不存在！");
			}
			if(receiptRefund.getAdjustMoney()==null)
				receiptRefund.setAdjustMoney(BigDecimal.ZERO);
			receiptRefund.setMoney(receiptRefund.getMoney().subtract(receiptRefund.getAdjustMoney()).add(adjustMoney));
			receiptRefund.setAdjustMoney(adjustMoney);
			receiptRefund.setReason(reason);
			hyReceiptRefundService.update(receiptRefund);
			json.setSuccess(true);
			json.setMsg("修改成功");
			json.setObj(receiptRefund);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(true);
			json.setMsg("修改失败");
			json.setObj(e);
		}
		return json;
	}
	
	@RequestMapping("receipt_refund/detail")
	@ResponseBody
	public Json receiptRefundDetail(Long id) {
		Json json = new Json();
		try {
			HyReceiptRefund receiptRefund = hyReceiptRefundService.find(id);
			if(receiptRefund==null) {
				throw new Exception("记录不存在");
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(receiptRefund);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	@RequestMapping("receipt_refund/audit")
	@ResponseBody
	public Json receiptRefundAudit(Long id,Integer status,BigDecimal adjustMoney,String reason,HttpSession session) {
		
		Json json = new Json();
		try {
			String username = (String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			
			if(id==null || status==null) {
				throw new Exception("缺少必要参数");
			}
			
			HyReceiptRefund receiptRefund = hyReceiptRefundService.find(id);
			if(receiptRefund==null) {
				throw new Exception("该记录不存在！");
			}
			
			if(receiptRefund.getStatus()!=0) {
				throw new Exception("该记录财务已审核");
			}
			if(receiptRefund.getAdjustMoney()==null)
				receiptRefund.setAdjustMoney(BigDecimal.ZERO);
			if(receiptRefund.getMoney()==null)
				receiptRefund.setMoney(BigDecimal.ZERO);
			receiptRefund.setMoney(receiptRefund.getMoney().subtract(receiptRefund.getAdjustMoney()).add(adjustMoney));
			receiptRefund.setAdjustMoney(adjustMoney);
			receiptRefund.setReason(reason);	
			
			receiptRefund.setStatus(status);
			receiptRefund.setCwAuditor(hyAdmin);
			hyReceiptRefundService.update(receiptRefund);
			json.setSuccess(true);
			json.setMsg("审核成功");
			json.setObj(receiptRefund);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("审核失败");
			json.setObj(e);
		}
		return json;
		
		
		
	}
	
	

}
