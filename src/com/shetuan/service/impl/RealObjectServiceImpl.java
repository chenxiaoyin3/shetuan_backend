package com.shetuan.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.shetuan.entity.Activity;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.entity.RealObject;
import com.shetuan.service.AudioService;
import com.shetuan.service.ImageService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.RealObjectService;
import com.shetuan.service.VideoService;

@Service("RealObjectServiceImpl")
public class RealObjectServiceImpl extends BaseServiceImpl<RealObject,Long> implements RealObjectService{
	@Override
	@Resource(name="realObjectDaoImpl")
	public void setBaseDao(BaseDao<RealObject,Long> baseDao) {
		super.setBaseDao(baseDao);
	}

	@Resource(name = "AudioServiceImpl")
	AudioService audioService;
	
	@Resource(name = "VideoServiceImpl")
	VideoService videoService;

	@Resource(name = "ImageServiceImpl")
	ImageService imageService;
	
	@Resource(name = "PeopleRelationServiceImpl")
	PeopleRelationService peopleRelationService;
	
	@Override
	public List<RealObject> getDetailByFilter(List<Filter> filters) {
		List<RealObject> realObjects = this.findList(null, filters, null);
		
		for(RealObject realObject : realObjects) {
			List<Filter> f = new ArrayList<Filter>();
			f.add(Filter.eq("entityId", realObject.getId()));
			f.add(Filter.eq("type", 5));
			realObject.setAudios(audioService.getDetailAndPeopleByFilter(f));
			realObject.setVideos(videoService.getDetailAndPeopleByFilter(f));
			realObject.setImages(imageService.getDetailAndPeopleByFilter(f));
			List<PeopleRelation> peopleRelations = peopleRelationService.findList(null, f, null);
			realObject.setRelatedPeople(peopleRelationService.addPeopleName(peopleRelations));
		}
		return realObjects;
	}
}


