package com.hongyu.service;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.BranchRecharge;

public interface BranchRechargeService extends BaseService<BranchRecharge, Long> {
	
	public Json branchRechargeAudit(Long id, String comment, Integer state, HttpSession session);

}
