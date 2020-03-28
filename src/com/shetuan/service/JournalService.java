package com.shetuan.service;

import java.util.HashMap;
import java.util.List;

import com.grain.service.BaseService;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.shetuan.entity.Journal;

public interface JournalService extends BaseService<Journal,Long>{

//	List<Journal> getDetailByFilter(List<Filter> filters);

	public Json getDetail(Long id);

	List<HashMap<String, Object>> getDetailByFilter(List<Filter> filters);

}
