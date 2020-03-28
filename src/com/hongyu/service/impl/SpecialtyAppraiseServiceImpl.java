package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.SpecialtyAppraiseDao;
import com.hongyu.entity.SpecialtyAppraise;
import com.hongyu.service.SpecialtyAppraiseService;

@Service(value = "specialtyAppraiseServiceImpl")
public class SpecialtyAppraiseServiceImpl extends BaseServiceImpl<SpecialtyAppraise, Long> implements SpecialtyAppraiseService {
	
	@Resource(name = "specialtyAppraiseDaoImpl")
	SpecialtyAppraiseDao specialtyAppraiseDaoImpl;
	

	@Resource(name = "specialtyAppraiseDaoImpl")
	public void setBaseDao(SpecialtyAppraiseDao dao){
		super.setBaseDao(dao);		
	}
}
