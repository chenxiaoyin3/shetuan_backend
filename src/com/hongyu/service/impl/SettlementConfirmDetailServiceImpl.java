package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.SettlementConfirmDetailDao;
import com.hongyu.entity.SettlementConfirmDetail;
import com.hongyu.service.SettlementConfirmDetailService;

@Service("settlementConfirmDetailServiceImpl")
public class SettlementConfirmDetailServiceImpl extends BaseServiceImpl<SettlementConfirmDetail, Long>
		implements SettlementConfirmDetailService {
	@Resource(name = "settlementConfirmDetailDaoImpl")
	SettlementConfirmDetailDao dao;

	@Resource(name = "settlementConfirmDetailDaoImpl")
	public void setBaseDao(SettlementConfirmDetailDao dao) {
		super.setBaseDao(dao);
	}
}