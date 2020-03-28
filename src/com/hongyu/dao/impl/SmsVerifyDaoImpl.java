package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SmsVerifyDao;
import com.hongyu.entity.SmsVerify;

@Repository("smsVerifyDaoImpl")
public class SmsVerifyDaoImpl extends BaseDaoImpl<SmsVerify, Long> implements SmsVerifyDao {
}