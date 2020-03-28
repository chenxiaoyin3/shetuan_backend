package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SettlementConfirmDao;
import com.hongyu.entity.SettlementConfirm;

@Repository("settlementConfirmDaoImpl")
public class SettlementConfirmDaoImpl extends BaseDaoImpl<SettlementConfirm, Long> implements SettlementConfirmDao {
}