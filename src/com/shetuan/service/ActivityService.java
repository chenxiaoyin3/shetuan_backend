package com.shetuan.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.grain.service.BaseService;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.entity.Activity;

public interface ActivityService extends BaseService<Activity,Long>{

	Json listView(Pageable pageable, String name, List<Long> organizationId, Integer type, Date startTime, Date endTime,
			String place, String organizationName);

	List<HashMap<String, Object>> getDetailByFilter(List<Filter> filters);

	Json getDetail(Long id);

	Json listView(Pageable pageable, String name, String organizationName, Integer type, Date startTime, Date endTime,
			String place);

}
