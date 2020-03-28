package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.TwiceConsumeStatisDao;
import com.hongyu.entity.TwiceConsumeStatis;
import com.hongyu.service.TwiceConsumeStatisService;

@Service("twiceConsumeStatisServiceImpl")
public class TwiceConsumeStatisServiceImpl extends BaseServiceImpl<TwiceConsumeStatis, Long>
		implements TwiceConsumeStatisService {
	@Resource(name = "twiceConsumeStatisDaoImpl")
	TwiceConsumeStatisDao dao;

	@Resource(name = "twiceConsumeStatisDaoImpl")
	public void setBaseDao(TwiceConsumeStatisDao dao) {
		super.setBaseDao(dao);
	}
}