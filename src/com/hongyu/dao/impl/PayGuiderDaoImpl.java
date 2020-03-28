package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayGuiderDao;
import com.hongyu.entity.PayGuider;

@Repository("payGuiderDaoImpl")
public class PayGuiderDaoImpl extends BaseDaoImpl<PayGuider, Long> implements PayGuiderDao {
}