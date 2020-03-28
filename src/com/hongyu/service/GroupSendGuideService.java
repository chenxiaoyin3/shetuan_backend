package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.GroupSendGuide;

public interface GroupSendGuideService extends BaseService<GroupSendGuide, Long> {
	public Json addOrder(Long id,Long guideId);
}
