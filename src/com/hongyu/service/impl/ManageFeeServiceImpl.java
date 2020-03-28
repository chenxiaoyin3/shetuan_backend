package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ManageFeeDao;
import com.hongyu.entity.ManageFee;
import com.hongyu.service.ManageFeeService;

@Service("manageFeeServiceImpl")
public class ManageFeeServiceImpl extends BaseServiceImpl<ManageFee, Long> implements ManageFeeService {
	@Resource(name = "manageFeeDaoImpl")
	ManageFeeDao dao;

	@Resource(name = "manageFeeDaoImpl")
	public void setBaseDao(ManageFeeDao dao) {
		super.setBaseDao(dao);
	}
}