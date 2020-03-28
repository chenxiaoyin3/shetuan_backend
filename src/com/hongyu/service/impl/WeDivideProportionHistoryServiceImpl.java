package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.WeDivideProportionHistoryDao;
import com.hongyu.entity.WeDivideProportionHistory;
import com.hongyu.service.WeDivideProportionHistoryService;

@Service("weDivideProportionHistoryServiceImpl")
public class WeDivideProportionHistoryServiceImpl
extends BaseServiceImpl<WeDivideProportionHistory, Long>
implements WeDivideProportionHistoryService{
	@Resource(name="weDivideProportionHistoryDaoImpl")
	WeDivideProportionHistoryDao weDivideProportionHistoryDaoImpl;
	  
	  @Resource(name="weDivideProportionHistoryDaoImpl")
	  public void setBaseDao(WeDivideProportionHistoryDao dao)
	  {
	    super.setBaseDao(dao);
	  }
}
