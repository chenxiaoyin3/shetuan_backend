package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PwbjdDao;
import com.hongyu.entity.PiaowubuJiudian;
import com.hongyu.service.PwbjdService;

@Service("pwbjdServiceImpl")
public class PwbjdServiceImpl extends BaseServiceImpl<PiaowubuJiudian, Long> implements PwbjdService{
	@Resource(name="pwbjdDaoImpl")
	PwbjdDao pwbjdDaoImpl;
	
	@Resource(name="pwbjdDaoImpl")
	public void setBaseDao(PwbjdDao dao) {
		super.setBaseDao(dao);
	}
}
