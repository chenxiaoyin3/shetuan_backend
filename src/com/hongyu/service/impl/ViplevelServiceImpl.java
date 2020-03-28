package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ViplevelDao;
import com.hongyu.entity.Viplevel;
import com.hongyu.service.ViplevelService;

@Service(value = "viplevelServiceImpl")
public class ViplevelServiceImpl extends BaseServiceImpl<Viplevel,Long> implements ViplevelService {
	@Resource(name = "viplevelDaoImpl")
	ViplevelDao dao;

	@Resource(name = "viplevelDaoImpl")
	public void setBaseDao(ViplevelDao dao) {
		super.setBaseDao(dao);
	}
}
