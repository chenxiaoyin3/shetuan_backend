package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayShareProfitDao;
import com.hongyu.entity.PayShareProfit;
import com.hongyu.service.PayShareProfitService;

@Service("payShareProfitServiceImpl")
public class PayShareProfitServiceImpl extends BaseServiceImpl<PayShareProfit, Long> implements PayShareProfitService {
	@Resource(name = "payShareProfitDaoImpl")
	PayShareProfitDao dao;

	@Resource(name = "payShareProfitDaoImpl")
	public void setBaseDao(PayShareProfitDao dao) {
		super.setBaseDao(dao);
	}
}