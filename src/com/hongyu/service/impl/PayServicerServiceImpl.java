package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayServicerDao;
import com.hongyu.entity.PayServicer;
import com.hongyu.service.PayServicerService;

@Service("payServicerServiceImpl")
public class PayServicerServiceImpl extends BaseServiceImpl<PayServicer, Long> implements PayServicerService {
	@Resource(name = "payServicerDaoImpl")
	PayServicerDao dao;

	@Resource(name = "payServicerDaoImpl")
	public void setBaseDao(PayServicerDao dao) {
		super.setBaseDao(dao);
	}
}