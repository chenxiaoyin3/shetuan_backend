package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PaySettlementDao;
import com.hongyu.entity.PaySettlement;

@Repository("paySettlementDaoImpl")
public class PaySettlementDaoImpl extends BaseDaoImpl<PaySettlement, Long> implements PaySettlementDao {
}