package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PwbjdfjjgkcDao;
import com.hongyu.entity.PiaowubuJdfjjgkc;
import com.hongyu.service.PwbjdfjjgkcService;
@Service("pwbjdfjjgkcServiceImpl")
public class PwbjdfjjgkcServiceImpl extends BaseServiceImpl<PiaowubuJdfjjgkc, Long> implements PwbjdfjjgkcService {
	@Resource(name="piaowubuJdfjjgkcDaoImpl")
	PwbjdfjjgkcDao pwbjdfjjgkcDao;
	
	@Resource(name="piaowubuJdfjjgkcDaoImpl")
	public void setBaseDao(PwbjdfjjgkcDao dao) {
		super.setBaseDao(dao);
	}
}
