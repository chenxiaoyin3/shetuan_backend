package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayDetailsBranchDao;
import com.hongyu.entity.PayDetailsBranch;
import com.hongyu.service.PayDetailsBranchService;

@Service("payDetailsBranchServiceImpl")
public class PayDetailsBranchServiceImpl extends BaseServiceImpl<PayDetailsBranch, Long>
		implements PayDetailsBranchService {
	@Resource(name = "payDetailsBranchDaoImpl")
	PayDetailsBranchDao dao;

	@Resource(name = "payDetailsBranchDaoImpl")
	public void setBaseDao(PayDetailsBranchDao dao) {
		super.setBaseDao(dao);
	}
}