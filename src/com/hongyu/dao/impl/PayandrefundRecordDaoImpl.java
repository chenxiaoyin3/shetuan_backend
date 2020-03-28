package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayandrefundRecordDao;
import com.hongyu.entity.PayandrefundRecord;

@Repository("payandrefundRecordDaoImpl")
public class PayandrefundRecordDaoImpl extends BaseDaoImpl<PayandrefundRecord, Long> implements PayandrefundRecordDao {

}
