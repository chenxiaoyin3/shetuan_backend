package com.hongyu.service;

import java.util.List;

import com.grain.service.BaseService;
import com.hongyu.entity.HyLabel;
import com.hongyu.entity.SpecialtySpecification;

public interface HyLabelService extends BaseService<HyLabel, Long>{
	List<SpecialtySpecification> getSpecificationsByLabelId(long id);

}
