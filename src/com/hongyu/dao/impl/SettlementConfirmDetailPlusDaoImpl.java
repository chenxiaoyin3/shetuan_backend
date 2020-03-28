package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SettlementConfirmDetailPlusDao;
import com.hongyu.entity.SettlementConfirmDetailPlus;

@Repository("settlementConfirmDetailPlusDaoImpl")
public class SettlementConfirmDetailPlusDaoImpl extends BaseDaoImpl<SettlementConfirmDetailPlus, Long>
		implements SettlementConfirmDetailPlusDao {
}