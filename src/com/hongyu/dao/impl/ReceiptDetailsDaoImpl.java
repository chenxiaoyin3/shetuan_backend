package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReceiptDetailsDao;
import com.hongyu.entity.ReceiptDetail;

@Repository("receiptDetailsDaoImpl")
public class ReceiptDetailsDaoImpl extends BaseDaoImpl<ReceiptDetail, Long> implements ReceiptDetailsDao {
}