package com.hongyu.controller.hzj03;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.service.SupplierDismissOrderApplyService;

/** */
@Controller
@RequestMapping("/dismissOrder/supplier")
public class SupplierDismissOrderApplySubmitController {
	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	SupplierDismissOrderApplyService supplierDismissOrderApplyService;
	
	/** 供应商提交驳回订单*/
	@RequestMapping("/submit")
	@ResponseBody
	public Json supplierDismissOrderSubmit(Long orderId,String comment, HttpSession session){
		Json json = new Json();
		
		try {
			json = supplierDismissOrderApplyService.addSupplierDismissOrderSubmit(orderId,comment,session);
		} catch (Exception e) {
			json.setMsg("操作失败");
			json.setSuccess(false);
			e.printStackTrace();
		}
		
		return json;
	}
}
