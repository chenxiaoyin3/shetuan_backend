package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.SmsVerifyDao;
import com.hongyu.entity.SmsVerify;
import com.hongyu.service.SmsVerifyService;

@Service("smsVerifyServiceImpl")
public class SmsVerifyServiceImpl extends BaseServiceImpl<SmsVerify, Long> implements SmsVerifyService {
	@Resource(name = "smsVerifyDaoImpl")
	SmsVerifyDao dao;

	@Resource(name = "smsVerifyDaoImpl")
	public void setBaseDao(SmsVerifyDao dao) {
		super.setBaseDao(dao);
	}
}