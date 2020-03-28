package com.shetuan.service;

import java.util.List;

import com.grain.service.BaseService;
import com.hongyu.Filter;
import com.shetuan.entity.Constitution;

public interface ConstitutionService extends BaseService<Constitution,Long>{

	List<Constitution> getDetailByFilter(List<Filter> filters);

}
