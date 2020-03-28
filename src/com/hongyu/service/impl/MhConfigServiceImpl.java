package com.hongyu.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.MhConfigDao;
import com.hongyu.entity.MhConfig;
import com.hongyu.service.MhConfigService;

@Service(value = "mhConfigServiceImpl")
public class MhConfigServiceImpl extends BaseServiceImpl<MhConfig, Long> implements MhConfigService{
	@Resource(name = "mhConfigDaoImpl")
	MhConfigDao mhConfigDao;
	
	@Resource(name = "mhConfigDaoImpl")
	public void setBaseDao(MhConfigDao dao){
		super.setBaseDao(dao);		
	}

	@Override
	public void updateConfig(MhConfig mhConfig) {
		// TODO Auto-generated method stub
		mhConfig.setUpdateTime(new Date());
		update(mhConfig);
	}

	@Override
	public MhConfig getConfig() {
		// TODO Auto-generated method stub
		List<MhConfig> list = findAll();
		if(list.size() == 0) {
			return new MhConfig();
		}
		MhConfig mhConfig = list.get(0);
		return mhConfig;
	}
}
