package com.hongyu.service;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.SupplierDismissOrderApply;

public interface SupplierDismissOrderApplyService extends BaseService<SupplierDismissOrderApply, Long> {
	Json addSupplierDismissOrderSubmit(Long orderId, String comment, HttpSession session) throws Exception;

	Json addSupplierDismissOrderAudit(Long id, String comment, Integer state, HttpSession session) throws Exception;
}