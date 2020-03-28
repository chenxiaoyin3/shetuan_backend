package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.SpecialtyCategoryModifyHistoryDao;
import com.hongyu.entity.SpecialtyCategoryModifyHistory;
import com.hongyu.service.SpecialtyCategoryModifyHistoryService;

@Service("specialtyCategoryModifyHistoryServiceImpl")
public class SpecialtyCategoryModifyHistoryServiceImpl 
extends BaseServiceImpl<SpecialtyCategoryModifyHistory, Long> 
implements SpecialtyCategoryModifyHistoryService {
	  @Resource(name="specialtyCategoryModifyHistoryDaoImpl")
	  SpecialtyCategoryModifyHistoryDao SpecialtyCategoryModifyHistoryDaoImpl;
	  
	  @Resource(name="specialtyCategoryModifyHistoryDaoImpl")
	  public void setBaseDao(SpecialtyCategoryModifyHistoryDao dao)
	  {
	    super.setBaseDao(dao);
	  }
}
