package com.shetuan.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.shetuan.entity.Activity;
import com.shetuan.entity.Organization;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.service.ActivityService;
import com.shetuan.service.AudioService;
import com.shetuan.service.ImageService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.VideoService;
@Service("ActivityServiceImpl")
public class ActivityServiceImpl extends BaseServiceImpl<Activity,Long> implements ActivityService{
	@Override
	@Resource(name="ActivityDaoImpl")
	public void setBaseDao(BaseDao<Activity,Long> baseDao) {
		super.setBaseDao(baseDao);
	}

	@Resource(name="OrganizationServiceImpl")
	OrganizationService organizationService;

	@Override
	public Json listView(Pageable pageable, String name, String organizationName, Integer type, @DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime, String place) {
		List<Long> organizationId = new ArrayList<Long>();
		if(organizationName != null) {
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.like("name", organizationName));
			List<Organization> organizations = organizationService.findList(null, filters, null);
			for(Organization organization : organizations) {
				organizationId.add(organization.getId());
			}
			if(organizationId.isEmpty()) {
				organizationId.add((long) 0);				
			}
		}
		return listView(pageable, name, organizationId, type, startTime, endTime, place, organizationName);
	}
	
	@Override
	public Json listView(Pageable pageable, String name, List<Long> organizationId, Integer type, @DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime, String place, String organizationName) {
		Json j = new Json();
		
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> geMap = new HashMap<String, Object>();
			Map<String, Object> leMap = new HashMap<String, Object>();
			
			map.put("name", name);
			map.put("type", type);
			map.put("place", place);
			geMap.put("startTime", startTime);
			leMap.put("startTime", endTime);
			
			List<Filter> filters = this.setFilterEqsAsMap(map);
			filters = this.addFilterGeMap(geMap, filters);
			filters = this.addFilterLeMap(leMap, filters);
			if(organizationName != null) filters.add(Filter.in("organizationId", organizationId));
			pageable.setFilters(filters);
			Page<Activity> page = this.findPage(pageable);

			Set<String> properties = new HashSet<String>() {{
				add("id");
				add("organizationId");
				add("name");
				add("place");
				add("startTime");
				add("endTime");
			}};
			HashMap<String,Object> hm=new HashMap<String,Object>();
			List<HashMap<String,Object>> result=
					this.getResultByObjectMapper(page.getRows(), "Activity-Json-Filter",properties);
			
			for(HashMap<String, Object> mHashMap:result) {
				String orgName = organizationService.find(Long.valueOf(String.valueOf(mHashMap.get("organizationId"))).longValue()).getName();
				mHashMap.put("organizationName", orgName);
				List<Filter> f = new ArrayList<Filter>();
				f.add(Filter.eq("entityId", Long.valueOf(String.valueOf(mHashMap.get("id"))).longValue()));
				f.add(Filter.eq("type", 2));
				List<HashMap<String, Object>> images = imageService.getDetailByFilter(f);
				if(images.isEmpty()) 
					mHashMap.put("image",null);
				else
					mHashMap.put("image", images.get(0).get("url"));
			}
			
			hm.put("total",page.getTotal());
			hm.put("pageNumber",page.getPageNumber());
			hm.put("pageSize",page.getPageSize());
			hm.put("result",result);
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(hm);
			
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
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
	public List<HashMap<String, Object>> getDetailByFilter(List<Filter> filters) {
		List<Activity> activities = this.findList(null, filters, null);
		
		for(Activity activity : activities) {
			List<Filter> f = new ArrayList<Filter>();
			f.add(Filter.eq("entityId", activity.getId()));
			f.add(Filter.eq("type", 2));
			activity.setAudios(audioService.getDetailAndPeopleByFilter(f));
			activity.setVideos(videoService.getDetailAndPeopleByFilter(f));
			activity.setImages(imageService.getDetailAndPeopleByFilter(f));
			List<PeopleRelation> peopleRelations = peopleRelationService.findList(null, f, null);
			activity.setRelatedPeople(peopleRelationService.addPeopleName(peopleRelations));
		}
		
		ObjectMapper mapper = new ObjectMapper();
		FilterProvider myFilter = new SimpleFilterProvider().addFilter("Activity-Json-Filter",
				SimpleBeanPropertyFilter.serializeAllExcept());
		mapper.setFilters(myFilter);
		
		List<HashMap<String, Object>> result=this.transferToHashMapList(mapper, activities);
		return result;
	}

	@Override
	public Json getDetail(Long id) {
		Json j = new Json();
		
		try {
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("id", id));
			List<HashMap<String, Object>> result = this.getDetailByFilter(filters);
			HashMap<String, Object> m = new HashMap<String, Object>();
			System.out.print(result.size());
			if(!result.isEmpty()) {
				m = result.get(0);
				Organization organization = organizationService.find(Long.valueOf(String.valueOf(m.get("organizationId"))).longValue());
				m.put("organizationName", organization.getName());
			}
			
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(m);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	
}
