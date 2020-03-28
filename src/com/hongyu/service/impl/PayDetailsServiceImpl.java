package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayDetailsDao;
import com.hongyu.entity.PayDetails;
import com.hongyu.service.PayDetailsService;

@Service("payDetailsServiceImpl")
public class PayDetailsServiceImpl extends BaseServiceImpl<PayDetails, Long> implements PayDetailsService {
	@Resource(name = "payDetailsDaoImpl")
	PayDetailsDao dao;

	@Resource(name = "payDetailsDaoImpl")
	public void setBaseDao(PayDetailsDao dao) {
		super.setBaseDao(dao);
	}
}