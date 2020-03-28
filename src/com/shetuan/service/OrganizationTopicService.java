package com.shetuan.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;

public interface OrganizationTopicService {
	public Json listView(Pageable pageable,String name, Boolean state, String creator, String place, Date startTime, Date endTime);
	
	public Json realObjectDetailById(Long id);
	
}
