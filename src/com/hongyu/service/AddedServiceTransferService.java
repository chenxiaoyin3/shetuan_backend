package com.hongyu.service;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.AddedServiceTransfer;

public interface AddedServiceTransferService extends BaseService<AddedServiceTransfer, Long> {
	public Json insertAddedValueAudit(Long id, String comment, Integer state, HttpSession session) throws Exception;
}