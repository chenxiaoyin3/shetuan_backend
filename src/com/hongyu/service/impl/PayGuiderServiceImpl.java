package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayGuiderDao;
import com.hongyu.entity.PayGuider;
import com.hongyu.service.PayGuiderService;

@Service("payGuiderServiceImpl")
public class PayGuiderServiceImpl extends BaseServiceImpl<PayGuider, Long> implements PayGuiderService {
	@Resource(name = "payGuiderDaoImpl")
	PayGuiderDao dao;

	@Resource(name = "payGuiderDaoImpl")
	public void setBaseDao(PayGuiderDao dao) {
		super.setBaseDao(dao);
	}
}