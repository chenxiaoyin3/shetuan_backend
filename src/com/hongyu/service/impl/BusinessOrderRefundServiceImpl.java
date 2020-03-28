package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BusinessOrderRefundDao;
import com.hongyu.entity.BusinessOrderRefund;
import com.hongyu.service.BusinessOrderRefundService;

@Service("businessOrderRefundServiceImpl")
public class BusinessOrderRefundServiceImpl extends BaseServiceImpl<BusinessOrderRefund, Long>
		implements BusinessOrderRefundService {
	@Resource(name = "businessOrderRefundDaoImpl")
	BusinessOrderRefundDao dao;

	@Resource(name = "businessOrderRefundDaoImpl")
	public void setBaseDao(BusinessOrderRefundDao dao) {
		super.setBaseDao(dao);
	}
}