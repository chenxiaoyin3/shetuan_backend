package com.shetuan.service;

import java.util.List;

import com.grain.service.BaseService;
import com.shetuan.entity.PeopleRelation;

public interface PeopleRelationService extends BaseService<PeopleRelation,Long>{

	List<PeopleRelation> addPeopleName(List<PeopleRelation> parms);

}
