package com.shetuan.service;

import java.util.HashMap;
import java.util.List;

import com.grain.service.BaseService;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.entity.Image;

public interface ImageService extends BaseService<Image,Long>{

	List<HashMap<String, Object>> getDetailByFilter(List<Filter> filter);

	Json getDetail(Long id);

	List<HashMap<String, Object>> getDetailAndPeopleByFilter(List<Filter> filter);

	Json listView(Pageable pageable, String name, String organizationName, String peopleName);

}