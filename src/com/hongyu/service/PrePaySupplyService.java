package com.hongyu.service;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.PrePaySupply;

public interface PrePaySupplyService extends BaseService<PrePaySupply, Long> {

	Json insertPrePaySupplyAudit(Long id, String comment, Integer state, HttpSession session) throws Exception;
}