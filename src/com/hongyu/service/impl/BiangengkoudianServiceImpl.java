package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BiangengkoudianDao;
import com.hongyu.entity.BiangengkoudianEntity;
import com.hongyu.service.BiangengkoudianService;
@Service(value = "biangengkoudianServiceImpl")
public class BiangengkoudianServiceImpl extends BaseServiceImpl<BiangengkoudianEntity, Long> implements BiangengkoudianService {
	@Resource(name = "biangengkoudianDaoImpl")
	BiangengkoudianDao dao;
	
	@Resource(name = "biangengkoudianDaoImpl")
	public void setBaseDao(BiangengkoudianDao dao){
		super.setBaseDao(dao);		
	}
}
