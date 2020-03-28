package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyLineRefundDao;
import com.hongyu.entity.HyLineRefund;
import com.hongyu.service.HyLineRefundService;
@Service(value = "hyLineRefundServiceImpl")
public class HyLineRefundServiceImpl extends BaseServiceImpl<HyLineRefund, Long> implements HyLineRefundService {
	@Resource(name = "hyLineRefundDaoImpl")
	HyLineRefundDao dao;
	
	@Resource(name = "hyLineRefundDaoImpl")
	public void setBaseDao(HyLineRefundDao dao){
		super.setBaseDao(dao);		
	}
}
