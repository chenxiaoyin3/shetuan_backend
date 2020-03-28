package com.hongyu.service;

import java.util.Date;

import com.grain.service.BaseService;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;

public interface HyGroupService extends BaseService<HyGroup, Long> {
	boolean groupDayExist(Date startDay, HyLine line, Long id,Boolean teamType);
}
