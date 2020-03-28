package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.WeDivideModelHistoryDao;
import com.hongyu.entity.WeDivideModelHistory;
import com.hongyu.service.WeDivideModelHistoryService;

@Service("weDivideModelHistoryServiceImpl")
public class WeDivideModelHistoryServiceImpl
extends BaseServiceImpl<WeDivideModelHistory, Long>
implements WeDivideModelHistoryService{
	@Resource(name="weDivideModelHistoryDaoImpl")
	WeDivideModelHistoryDao weDivideModelHistoryDaoImpl;
	  
	  @Resource(name="weDivideModelHistoryDaoImpl")
	  public void setBaseDao(WeDivideModelHistoryDao dao)
	  {
	    super.setBaseDao(dao);
	  }
}
