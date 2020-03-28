package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.WeDivideModelDao;
import com.hongyu.entity.WeDivideModel;
import com.hongyu.service.WeDivideModelService;

@Service("weDivideModelServiceImpl")
public class WeDivideModelServiceImpl extends BaseServiceImpl<WeDivideModel, Long>
implements WeDivideModelService{
	@Resource(name="weDivideModelDaoImpl")
	WeDivideModelDao weDivideModelDaoImpl;
	  
	  @Resource(name="weDivideModelDaoImpl")
	  public void setBaseDao(WeDivideModelDao dao)
	  {
	    super.setBaseDao(dao);
	  }
}
