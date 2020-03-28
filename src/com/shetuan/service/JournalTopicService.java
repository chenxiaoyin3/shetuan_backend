package com.shetuan.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;

public interface JournalTopicService {
	public Json list(Pageable pageable, String name, String organizationName, Date startTime, Date endTime,
			boolean state);
}
