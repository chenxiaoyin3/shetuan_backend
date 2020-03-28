package com.shetuan.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.shetuan.entity.People;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.PeopleService;
@Service("PeopleRelationServiceImpl")
public class PeopleRelationServiceImpl extends BaseServiceImpl<PeopleRelation,Long> implements PeopleRelationService{
	@Override
	@Resource(name="PeopleRelationDaoImpl")
	public void setBaseDao(BaseDao<PeopleRelation,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
	
	@Resource(name="PeopleServiceImpl")
	PeopleService peopleService;
	
	@Override
	public List<PeopleRelation> addPeopleName(List<PeopleRelation> parms) {
		
		for(PeopleRelation peopleRelation : parms) {
			People people = peopleService.find(peopleRelation.getPeopleId());
			peopleRelation.setDescription(people.getDescription());
			peopleRelation.setPeopleName(people.getPeopleName());
		}
		return parms;
	}
}
