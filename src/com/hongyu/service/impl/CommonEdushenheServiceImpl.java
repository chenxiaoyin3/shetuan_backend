package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CommonEdushenheDao;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.service.CommonEdushenheService;
@Service(value = "commonEdushenheServiceImpl")
public class CommonEdushenheServiceImpl extends BaseServiceImpl<CommonShenheedu, Long> implements CommonEdushenheService {
	@Resource(name = "commonEdushenheDaoImpl")
	CommonEdushenheDao dao;
	
	@Resource(name = "commonEdushenheDaoImpl")
	public void setBaseDao(CommonEdushenheDao dao){
		super.setBaseDao(dao);		
	}
}
