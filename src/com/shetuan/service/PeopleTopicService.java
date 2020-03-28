package com.shetuan.service;

import com.hongyu.Json;
import com.hongyu.Pageable;

public interface PeopleTopicService {
	public Json list(Pageable pageable,String name,String organizationName);

	public Json detailById(Long id);
}
