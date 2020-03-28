package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.XuqianDao;
import com.hongyu.entity.XuqianEntity;
import com.hongyu.service.XuqianService;
@Service(value = "xuqianServiceImpl")
public class XuqianServiceImpl extends BaseServiceImpl<XuqianEntity, Long> implements XuqianService {
	@Resource(name = "xuqianDaoImpl")
	XuqianDao dao;
	
	@Resource(name = "xuqianDaoImpl")
	public void setBaseDao(XuqianDao dao){
		super.setBaseDao(dao);		
	}
}
