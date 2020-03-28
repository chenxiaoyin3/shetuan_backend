package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.RegulateGuideDao;
import com.hongyu.entity.RegulateGuide;
import com.hongyu.service.RegulateGuideService;
@Service("regulateGuideServiceImpl")
public class RegulateGuideServiceImpl extends BaseServiceImpl<RegulateGuide, Long> implements RegulateGuideService {
	@Resource(name="regulateGuideDaoImpl")
	RegulateGuideDao dao;
	
	@Resource(name="regulateGuideDaoImpl")
	public void setBaseDao(RegulateGuideDao dao) {
		super.setBaseDao(dao);
	}
}
