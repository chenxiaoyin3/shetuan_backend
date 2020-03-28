package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.MhSettingDao;
import com.hongyu.dao.MhSettingHistoryDao;
import com.hongyu.entity.MhSetting;
import com.hongyu.entity.MhSettingHistory;
import com.hongyu.service.MhSettingHistoryService;
import com.hongyu.service.MhSettingService;

@Service("mhSettingHistoryServiceImpl")
public class MhSettingHistoryServiceImpl extends BaseServiceImpl<MhSettingHistory, Long> implements MhSettingHistoryService{
	@Resource(name="mhSettingHistoryDaoImpl")
	MhSettingHistoryDao mhSettingDaoImpl;
	  
	@Resource(name="mhSettingHistoryDaoImpl")
	public void setBaseDao(MhSettingHistoryDao dao)
	{
	    super.setBaseDao(dao);
	}
}
