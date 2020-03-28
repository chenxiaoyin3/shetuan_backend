package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.SpecialtyLostDao;
import com.hongyu.entity.SpecialtyLost;
import com.hongyu.service.SpecialtyLostService;

@Service("specialtyLostServiceImpl")
public class SpecialtyLostServiceImpl extends BaseServiceImpl<SpecialtyLost, Long> implements SpecialtyLostService {
	@Resource(name="specialtyLostDaoImpl")
	SpecialtyLostDao specialtyLostDaoImpl;
	  
	  @Resource(name="specialtyLostDaoImpl")
	  public void setBaseDao(SpecialtyLostDao dao)
	  {
	    super.setBaseDao(dao);
	  }
}
