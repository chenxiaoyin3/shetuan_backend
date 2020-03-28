package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.MhFuSettingDao;
import com.hongyu.dao.MhSettingDao;
import com.hongyu.entity.MhFuSetting;
import com.hongyu.entity.MhSetting;
import com.hongyu.service.MhFuSettingService;
import com.hongyu.service.MhSettingService;

@Service("mhFuSettingServiceImpl")
public class MhFuSettingServiceImpl extends BaseServiceImpl<MhFuSetting, Long> implements MhFuSettingService{
	@Resource(name="mhFuSettingDaoImpl")
	MhFuSettingDao mhFuSettingDaoImpl;
	  
	@Resource(name="mhFuSettingDaoImpl")
	public void setBaseDao(MhFuSettingDao dao)
	{
	    super.setBaseDao(dao);
	}
}
