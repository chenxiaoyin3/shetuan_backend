package com.shetuan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.shetuan.entity.Audio;
import com.shetuan.entity.Image;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.service.AudioService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.PeopleService;
@Service("AudioServiceImpl")
public class AudioServiceImpl extends BaseServiceImpl<Audio,Long> implements AudioService{
	@Override
	@Resource(name="AudioDaoImpl")
	public void setBaseDao(BaseDao<Audio,Long> baseDao) {
		super.setBaseDao(baseDao);
	}

	@Override
	public List<HashMap<String, Object>> getDetailByFilter(List<Filter> filter) {
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String,Object>>();
		List<Audio> audios = this.findList(null, filter, null);
		
		return result;
	}
	
	@Resource(name="PeopleRelationServiceImpl")
	PeopleRelationService peopleRelationService;

	@Resource(name = "PeopleServiceImpl")
	PeopleService peopleService;

	@Override
	public List<Audio> getDetailAndPeopleByFilter(List<Filter> filter) {
		
		List<Audio> audios = this.findList(null, filter, null);

		for(Audio audio : audios) {
			List<Filter> f = new ArrayList<Filter>();
			f.add(Filter.eq("entityId", audio.getId()));
			f.add(Filter.eq("type", 7));
			List<PeopleRelation> peopleRelations = peopleRelationService.findList(null, f, null);
			audio.setRelatedPeople(peopleRelationService.addPeopleName(peopleRelations));
		}
		
		
		return audios;
	}
}
