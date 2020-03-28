package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.TwiceConsumeRecordDao;
import com.hongyu.entity.TwiceConsumeRecord;
import com.hongyu.service.TwiceConsumeRecordService;

@Service("twiceConsumeRecordServiceImpl")
public class TwiceConsumeRecordServiceImpl extends BaseServiceImpl<TwiceConsumeRecord, Long>
		implements TwiceConsumeRecordService {
	@Resource(name = "twiceConsumeRecordDaoImpl")
	TwiceConsumeRecordDao dao;

	@Resource(name = "twiceConsumeRecordDaoImpl")
	public void setBaseDao(TwiceConsumeRecordDao dao) {
		super.setBaseDao(dao);
	}
}