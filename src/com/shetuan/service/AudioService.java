package com.shetuan.service;

import java.util.HashMap;
import java.util.List;

import com.grain.service.BaseService;
import com.hongyu.Filter;
import com.shetuan.entity.Audio;

public interface AudioService extends BaseService<Audio,Long>{

	List<HashMap<String, Object>> getDetailByFilter(List<Filter> filter);

	List<Audio> getDetailAndPeopleByFilter(List<Filter> filter);

}
