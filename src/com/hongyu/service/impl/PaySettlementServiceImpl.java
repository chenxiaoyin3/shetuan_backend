package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PaySettlementDao;
import com.hongyu.entity.PaySettlement;
import com.hongyu.service.PaySettlementService;

@Service("paySettlementServiceImpl")
public class PaySettlementServiceImpl extends BaseServiceImpl<PaySettlement, Long> implements PaySettlementService {
	@Resource(name = "paySettlementDaoImpl")
	PaySettlementDao dao;

	@Resource(name = "paySettlementDaoImpl")
	public void setBaseDao(PaySettlementDao dao) {
		super.setBaseDao(dao);
	}
}