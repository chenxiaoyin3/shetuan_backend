package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HySpecialtyLineLabelDao;
import com.hongyu.entity.HySpecialtyLineLabel;
import com.hongyu.service.HySpecialtyLineLabelService;

@Service("hySpecialtyLineLabelServiceImpl")
public class HySpecialtyLineLabelServiceImpl extends BaseServiceImpl<HySpecialtyLineLabel, Long> implements HySpecialtyLineLabelService{

	@Resource(name = "hySpecialtyLineDaoImpl")
	HySpecialtyLineLabelDao dao;
	
	@Resource(name = "hySpecialtyLineDaoImpl")
	public void setBaseDao(HySpecialtyLineLabelDao dao){
		super.setBaseDao(dao);		
	}

}
