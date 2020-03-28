package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayDepositDao;
import com.hongyu.entity.PayDeposit;
import com.hongyu.service.PayDepositService;

@Service("payDepositServiceImpl")
public class PayDepositServiceImpl extends BaseServiceImpl<PayDeposit, Long> implements PayDepositService {
	@Resource(name = "payDepositDaoImpl")
	PayDepositDao dao;

	@Resource(name = "payDepositDaoImpl")
	public void setBaseDao(PayDepositDao dao) {
		super.setBaseDao(dao);
	}
}