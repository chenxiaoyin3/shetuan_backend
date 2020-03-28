package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.GystuiyajinDao;
import com.hongyu.entity.Gysfzrtuichu;
import com.hongyu.service.GystuiyajinService;
@Service("gysfzrtuichuServiceImpl")
public class GysfzrtuichuServiceImpl extends BaseServiceImpl<Gysfzrtuichu, Long> implements GystuiyajinService {
	@Resource(name = "gystuiyajinDaoImpl")
	GystuiyajinDao dao;

	@Resource(name = "gystuiyajinDaoImpl")
	public void setBaseDao(GystuiyajinDao dao) {
		super.setBaseDao(dao);
	}
}
