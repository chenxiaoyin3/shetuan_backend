package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.SettlementConfirmDao;
import com.hongyu.entity.SettlementConfirm;
import com.hongyu.service.SettlementConfirmService;

@Service("settlementConfirmServiceImpl")
public class SettlementConfirmServiceImpl extends BaseServiceImpl<SettlementConfirm, Long>
		implements SettlementConfirmService {
	@Resource(name = "settlementConfirmDaoImpl")
	SettlementConfirmDao dao;

	@Resource(name = "settlementConfirmDaoImpl")
	public void setBaseDao(SettlementConfirmDao dao) {
		super.setBaseDao(dao);
	}
}