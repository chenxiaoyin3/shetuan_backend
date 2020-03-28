package com.shetuan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

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
import com.shetuan.entity.Image;
import com.shetuan.entity.Organization;
import com.shetuan.entity.People;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.entity.RealObject;
import com.shetuan.entity.Video;
import com.shetuan.service.ActivityService;
import com.shetuan.service.ConstitutionService;
import com.shetuan.service.HistoricalDataIndexService;
import com.shetuan.service.LiteratureService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.PeopleService;
import com.shetuan.service.RealObjectService;
import com.shetuan.service.VideoService;
@Service("VideoServiceImpl")
public class VideoServiceImpl extends BaseServiceImpl<Video,Long> implements VideoService{
	@Override
	@Resource(name="VideoDaoImpl")
	public void setBaseDao(BaseDao<Video,Long> baseDao) {
		super.setBaseDao(baseDao);
	}

	@Resource(name = "PeopleServiceImpl")
	PeopleService peopleService;

	@Resource(name = "ActivityServiceImpl")
	ActivityService activityService;
	
	@Resource(name = "ConstitutionServiceImpl")
	ConstitutionService constitutionService;

	@Resource(name = "HistoricalDataIndexServiceImpl")
	HistoricalDataIndexService historicalDataIndexService;
	
	@Resource(name = "LiteratureServiceImpl")
	LiteratureService literatureService;
	
	@Resource(name = "RealObjectServiceImpl")
	RealObjectService realObjectService;
	
	@Resource(name="OrganizationServiceImpl")
	OrganizationService organizationService;
	
	@Resource(name="PeopleRelationServiceImpl")
	PeopleRelationService peopleRelationService;
	
	@Override
	public Json listView(Pageable pageable, String name, String organizationName,  String peopleName) {
		Json j = new Json();
		
		try {
			HashMap<String,Object> hm=new HashMap<String,Object>();
			List<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();
			List<Filter> filters = new ArrayList<Filter>();
			Boolean flag = true;

			if(null != name) {
				filters.add(Filter.like("name", name));
			}
			
			if(null != organizationName) {
				List<Long> organizationIds = new ArrayList<Long>();
				List<Filter> f = new ArrayList<Filter>();
				f.add(Filter.like("name", organizationName));
				List<Organization> organizations = organizationService.findList(null, f, null);
				for(Organization organization : organizations) {
					organizationIds.add(organization.getId());
				}
				if(organizationIds.isEmpty() == false) {
					List<Long> videoIds = new ArrayList<Long>();
					List<Filter> filter = new ArrayList<Filter>();
					List<Video> videosList = this.findList(null, filter, null);
					
					// activity
					f.clear();
					f.add(Filter.in("organizationId", organizationIds));	
					List<Activity> activities = activityService.findList(null, f, null);
					List<Long> activityIds = new ArrayList<Long>();
					for(Activity activity:activities) {
						activityIds.add(activity.getId());
					}
					if(activityIds.isEmpty() == false) {
						filter.clear();
						videosList.clear();
						filter.add(Filter.in("entityId", activityIds));	
						filter.add(Filter.eq("type", 2));
						videosList = this.findList(null, filter, null);
						for(Video video:videosList) {
							videoIds.add(video.getId());
						}	
					}
					
					// RealObject
					f.clear();
					f.add(Filter.in("organizationId", organizationIds));	
					List<RealObject> realObjects = realObjectService.findList(null, f, null);
					List<Long> realObjectIds = new ArrayList<Long>();
					for(RealObject realObject:realObjects) {
						realObjectIds.add(realObject.getId());
					}
					if(realObjectIds.isEmpty() == false) {
						filter.clear();
						videosList.clear();
						filter.add(Filter.in("entityId", realObjectIds));	
						filter.add(Filter.eq("type", 5));
						videosList = this.findList(null, filter, null);
						for(Video video:videosList) {
							videoIds.add(video.getId());
						}	
					}
					
					if(videoIds.isEmpty() == false) {
						filters.add(Filter.in("id", videoIds));
					}
					else {
						flag = false;
					}
				}
				else {
					flag = false;
				}
			}
			
			//find videos that correlate with people
			if(null != peopleName) {
				List<Filter> peopleRelationFilters = new ArrayList<>();
				peopleRelationFilters.add(Filter.like("peopleName", peopleName));
				List<People> peoples = peopleService.findList(null, peopleRelationFilters, null);
				List<Long> peopleId = new ArrayList<Long>();
				for(People people : peoples) {
					peopleId.add(people.getId());
				}
				if(peopleId.isEmpty() == false) {
					peopleRelationFilters.clear();
					peopleRelationFilters.add(Filter.in("peopleId", peopleId));
					peopleRelationFilters.add(Filter.eq("type", 8));
					List<Long> videoIds = new ArrayList<Long>();
					List<PeopleRelation> peopleRelations = peopleRelationService.findList(null, peopleRelationFilters, null);
					for(PeopleRelation pr : peopleRelations) {
						videoIds.add(pr.getEntityId());
					}
					if(videoIds.isEmpty() == false) {
						filters.add(Filter.in("id", videoIds));
					}
					else {
						flag = false;
					}
				}
				else {
					flag = false;
				}
				
			}

			if(flag == false) { // 不符合筛选条件
				hm.put("total",0);
				hm.put("pageNumber",1);
				hm.put("pageSize",10);
				hm.put("result",result);
				j.setSuccess(true);
				j.setMsg("获取成功");
				j.setObj(hm);
				return j;
			}
			
			if(filters.isEmpty() == false) {
				pageable.setFilters(filters);
			}
			Page<Video> page = this.findPage(pageable);

			for(Video video : page.getRows()) {
				List<Filter> f = new ArrayList<Filter>();
				f.add(Filter.eq("entityId", video.getId()));
				f.add(Filter.eq("type", 8));
				List<PeopleRelation> peopleRelations = peopleRelationService.findList(null, f, null);
				video.setRelatedPeople(peopleRelationService.addPeopleName(peopleRelations));
			}
			
			Set<String> properties = new HashSet<String>() {{
				add("id");
				add("name");
				add("url");
				add("description");
				add("relatedPeople");
			}};
			result = this.getResultByObjectMapper(page.getRows(), "Video-Json-Filter",properties);
			
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
	

	@Override
	public List<HashMap<String, Object>> getDetailByFilter(List<Filter> filter) {
		List<Video> videos = this.findList(null, filter, null);
		
		ObjectMapper mapper = new ObjectMapper();
		FilterProvider myFilter = new SimpleFilterProvider().addFilter("Video-Json-Filter",
				SimpleBeanPropertyFilter.serializeAllExcept());
		mapper.setFilters(myFilter);
		
		List<HashMap<String, Object>> result=this.transferToHashMapList(mapper, videos);
		
		return result;
	}

	@Override
	public List<HashMap<String, Object>> getDetailAndPeopleByFilter(List<Filter> filter) {
		List<Video> videos = this.findList(null, filter, null);

		for(Video video : videos) {
			List<Filter> f = new ArrayList<Filter>();
			f.add(Filter.eq("entityId", video.getId()));
			f.add(Filter.eq("type", 8));
			List<PeopleRelation> peopleRelations = peopleRelationService.findList(null, f, null);
			video.setRelatedPeople(peopleRelationService.addPeopleName(peopleRelations));
		}
		
		ObjectMapper mapper = new ObjectMapper();
		FilterProvider myFilter = new SimpleFilterProvider().addFilter("Video-Json-Filter",
				SimpleBeanPropertyFilter.serializeAllExcept());
		mapper.setFilters(myFilter);
		
		List<HashMap<String, Object>> result=this.transferToHashMapList(mapper, videos);
		
		return result;
	}
	
	@Override
	public Json getDetail(Long id) {
		Json j = new Json();
		
		try {
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("id", id));
			List<HashMap<String, Object>> result = this.getDetailAndPeopleByFilter(filters);
			HashMap<String, Object> m = new HashMap<String, Object>();
			
			if(!result.isEmpty()) {
				m = result.get(0);
//				Organization organization = organizationService.find(Long.valueOf(String.valueOf(m.get("organizationId"))).longValue());
//				m.put("organizationName", organization.getName());
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
