package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.LineCatagoryDao;
import com.hongyu.entity.LineCatagoryEntity;
import com.hongyu.service.LineCatagoryService;
@Service("lineCatagoryServiceImpl")
public class LineCatagoryServiceImpl extends BaseServiceImpl<LineCatagoryEntity, Long> implements LineCatagoryService {
	@Resource(name = "lineCatagoryDaoImpl")
	LineCatagoryDao dao;

	@Resource(name = "lineCatagoryDaoImpl")
	public void setBaseDao(LineCatagoryDao dao) {
		super.setBaseDao(dao);
	}
}
