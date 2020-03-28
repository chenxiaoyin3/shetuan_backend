package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.MhAppraiseDao;
import com.hongyu.dao.SpecialtyAppraiseDao;
import com.hongyu.entity.MhAppraise;
import com.hongyu.entity.SpecialtyAppraise;
import com.hongyu.service.MhAppraiseService;
import com.hongyu.service.SpecialtyAppraiseService;

@Service(value = "mhAppraiseServiceImpl")
public class MhAppraiseServiceImpl extends BaseServiceImpl<MhAppraise, Long> implements MhAppraiseService {
	
	@Resource(name = "mhAppraiseDaoImpl")
	MhAppraiseDao mhAppraiseDaoImpl;
	

	@Resource(name = "mhAppraiseDaoImpl")
	public void setBaseDao(MhAppraiseDao dao){
		super.setBaseDao(dao);		
	}
}
