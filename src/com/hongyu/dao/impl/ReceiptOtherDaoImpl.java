package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptOtherDao;
import com.hongyu.entity.ReceiptOther;

@Repository("receiptOtherDaoImpl")
public class ReceiptOtherDaoImpl extends BaseDaoImpl<ReceiptOther, Long> implements ReceiptOtherDao {
}