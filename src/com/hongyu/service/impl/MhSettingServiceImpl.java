package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.MhSettingDao;
import com.hongyu.entity.MhSetting;
import com.hongyu.service.MhSettingService;

@Service("mhSettingServiceImpl")
public class MhSettingServiceImpl extends BaseServiceImpl<MhSetting, Long> implements MhSettingService{
	@Resource(name="mhSettingDaoImpl")
	MhSettingDao mhSettingDaoImpl;
	  
	@Resource(name="mhSettingDaoImpl")
	public void setBaseDao(MhSettingDao dao)
	{
	    super.setBaseDao(dao);
	}
}
