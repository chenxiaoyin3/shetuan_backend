package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyReceiptRefundDao;
import com.hongyu.entity.HyReceiptRefund;

@Repository("hyReceiptRefundDaoImpl")
public class HyReceiptRefundDaoImpl extends BaseDaoImpl<HyReceiptRefund, Long> implements HyReceiptRefundDao {

}
