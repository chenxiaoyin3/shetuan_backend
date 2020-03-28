package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.RefundRecordsDao;
import com.hongyu.entity.RefundRecords;

@Repository("refundRecordsDaoImpl")
public class RefundRecordsDaoImpl extends BaseDaoImpl<RefundRecords, Long> implements RefundRecordsDao {
}