package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.PiaowubuGongyingshang;
import com.hongyu.dao.PiaowubuGongyingshangDao;
import com.hongyu.service.PiaowubuGongyingshangService;
@Service("piaowubuGongyingshangServiceImpl")
public class PiaowubuGongyingshangServiceImpl extends BaseServiceImpl<PiaowubuGongyingshang, Long>
		implements PiaowubuGongyingshangService {
	@Resource(name = "piaowubuGongyingshangDaoImpl")
	PiaowubuGongyingshangDao dao;

	@Resource(name = "piaowubuGongyingshangDaoImpl")
	public void setBaseDao(PiaowubuGongyingshangDao dao) {
		super.setBaseDao(dao);
	}
}
