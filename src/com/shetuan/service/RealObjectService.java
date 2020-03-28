package com.shetuan.service;

import java.util.List;

import com.grain.service.BaseService;
import com.hongyu.Filter;
import com.shetuan.entity.RealObject;

public interface RealObjectService extends BaseService<RealObject,Long>{
	
	List<RealObject> getDetailByFilter(List<Filter> filters);

}
