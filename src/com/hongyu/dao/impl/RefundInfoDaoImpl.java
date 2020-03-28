package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.RefundInfoDao;
import com.hongyu.entity.RefundInfo;

@Repository("refundInfoDaoImpl")
public class RefundInfoDaoImpl extends BaseDaoImpl<RefundInfo, Long> implements RefundInfoDao {
}