package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.SettlementConfirmDetailPlusDao;
import com.hongyu.entity.SettlementConfirmDetailPlus;
import com.hongyu.service.SettlementConfirmDetailPlusService;

@Service("settlementConfirmDetailPlusServiceImpl")
public class SettlementConfirmDetailPlusServiceImpl extends BaseServiceImpl<SettlementConfirmDetailPlus, Long>
		implements SettlementConfirmDetailPlusService {
	@Resource(name = "settlementConfirmDetailPlusDaoImpl")
	SettlementConfirmDetailPlusDao dao;

	@Resource(name = "settlementConfirmDetailPlusDaoImpl")
	public void setBaseDao(SettlementConfirmDetailPlusDao dao) {
		super.setBaseDao(dao);
	}
}