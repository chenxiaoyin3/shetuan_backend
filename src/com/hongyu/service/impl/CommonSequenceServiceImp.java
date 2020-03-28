package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CommonSequenceDao;
import com.hongyu.entity.CommonSequence;
import com.hongyu.service.CommonSequenceService;
@Service(value = "commonSequenceServiceImp")
public class CommonSequenceServiceImp extends BaseServiceImpl<CommonSequence, Long> implements CommonSequenceService {
	@Resource(name = "commonSequenceDaoImp")
	CommonSequenceDao dao;
	
	@Resource(name = "commonSequenceDaoImp")
	public void setBaseDao(CommonSequenceDao dao){
		super.setBaseDao(dao);		
	}
}
