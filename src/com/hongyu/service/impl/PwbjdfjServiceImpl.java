package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PwbjdfjDao;
import com.hongyu.entity.PiaowubuJiudianfangjian;
import com.hongyu.service.PwbjdfjService;
@Service("pwbjdfjServiceImpl")
public class PwbjdfjServiceImpl extends BaseServiceImpl<PiaowubuJiudianfangjian, Long> implements PwbjdfjService {
	@Resource(name="piaowubuJdfjDaoImpl")
	PwbjdfjDao pwbjdfjDao;
	
	@Resource(name="piaowubuJdfjDaoImpl")
	public void setBaseDao(PwbjdfjDao dao) {
		super.setBaseDao(dao);
	}
}
