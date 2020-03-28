package com.shetuan.service;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.shetuan.entity.RealObjectSource;

public interface RealObjectSourceService extends BaseService<RealObjectSource,Long> {
	
	public Json addRealObjectSource(String sourceType,Long realObjectID, String name, String url);

	public Json editRealObjectSource(String sourceType, Long realObjectSourceID, String name, String url);
}