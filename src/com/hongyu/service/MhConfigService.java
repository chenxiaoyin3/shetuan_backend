package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.MhConfig;

public interface MhConfigService extends BaseService<MhConfig, Long>{

	//更新配置
	public void updateConfig(MhConfig mhConfig);
	//拿到配置
	public MhConfig getConfig();
	
}
