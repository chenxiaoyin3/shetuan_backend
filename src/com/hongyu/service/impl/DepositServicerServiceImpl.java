package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.DepositServicerDao;
import com.hongyu.entity.DepositServicer;
import com.hongyu.service.DepositServicerService;

@Service("depositServicerServiceImpl")
public class DepositServicerServiceImpl extends BaseServiceImpl<DepositServicer, Long>
		implements DepositServicerService {
	@Resource(name = "depositServicerDaoImpl")
	DepositServicerDao dao;

	@Resource(name = "depositServicerDaoImpl")
	public void setBaseDao(DepositServicerDao dao) {
		super.setBaseDao(dao);
	}
}