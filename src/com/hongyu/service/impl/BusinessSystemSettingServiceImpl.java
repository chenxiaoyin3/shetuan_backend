package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BusinessSystemSettingDao;
import com.hongyu.entity.BusinessSystemSetting;
import com.hongyu.service.BusinessSystemSettingService;

@Service("businessSystemSettingServiceImpl")
public class BusinessSystemSettingServiceImpl extends BaseServiceImpl<BusinessSystemSetting, Long> implements BusinessSystemSettingService{
	@Resource(name="businessSystemSettingDaoImpl")
	BusinessSystemSettingDao businessSystemSettingDaoImpl;
	  
	@Resource(name="businessSystemSettingDaoImpl")
	public void setBaseDao(BusinessSystemSettingDao dao)
	{
	    super.setBaseDao(dao);
	}
}
