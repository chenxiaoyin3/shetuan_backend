package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyLineTransportDao;
import com.hongyu.entity.TransportEntity;
import com.hongyu.service.TransportService;
@Service(value = "transportServiceImpl")
public class TransportServiceImpl extends BaseServiceImpl<TransportEntity, Long> implements TransportService {
	@Resource(name = "transportDaoImp")
	HyLineTransportDao dao;
	
	@Resource(name = "transportDaoImp")
	public void setBaseDao(HyLineTransportDao dao){
		super.setBaseDao(dao);		
	}
}
