package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BusinessSettingHistoryDao;
import com.hongyu.entity.BusinessSettingHistory;
import com.hongyu.service.BusinessSettingHistoryService;

@Service("businessSettingHistoryServiceImpl")
public class BusinessSettingHistoryServiceImpl 
extends BaseServiceImpl<BusinessSettingHistory, Long> 
implements BusinessSettingHistoryService {
	
	@Resource(name="businessSettingHistoryDaoImpl")
	BusinessSettingHistoryDao businessSettingHistoryDaoImpl;
	  
	  @Resource(name="businessSettingHistoryDaoImpl")
	  public void setBaseDao(BusinessSettingHistoryDao dao)
	  {
	    super.setBaseDao(dao);
	  }
}
