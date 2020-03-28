package com.hongyu.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.BranchPayServicer;
import com.hongyu.entity.PayDetailsBranch;

public interface BranchPayServicerService extends BaseService<BranchPayServicer, Long> {
	public Json addbranchPayServicer(List<PayDetailsBranch> list, HttpSession session) throws Exception;
}