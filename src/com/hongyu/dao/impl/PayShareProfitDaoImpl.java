package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayShareProfitDao;
import com.hongyu.entity.PayShareProfit;

@Repository("payShareProfitDaoImpl")
public class PayShareProfitDaoImpl extends BaseDaoImpl<PayShareProfit, Long> implements PayShareProfitDao {
}