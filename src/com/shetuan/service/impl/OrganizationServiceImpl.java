package com.shetuan.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.poi.common.usermodel.Fill;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
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
import com.shetuan.entity.Constitution;
import com.shetuan.entity.HistoricalDataIndex;
import com.shetuan.entity.Journal;
import com.shetuan.entity.Literature;
import com.shetuan.entity.OfficePlace;
import com.shetuan.entity.Organization;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.entity.RealObject;
import com.shetuan.service.ActivityService;
import com.shetuan.service.AudioService;
import com.shetuan.service.ConstitutionService;
import com.shetuan.service.HistoricalDataIndexService;
import com.shetuan.service.ImageService;
import com.shetuan.service.JournalService;
import com.shetuan.service.LiteratureService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.RealObjectService;
import com.shetuan.service.VideoService;

@Service("OrganizationServiceImpl")
public class OrganizationServiceImpl extends BaseServiceImpl<Organization, Long> implements OrganizationService {
	@Override
	@Resource(name = "OrganizationDaoImpl")
	public void setBaseDao(BaseDao<Organization, Long> baseDao) {
		super.setBaseDao(baseDao);
	}

	@SuppressWarnings("serial")
	@Override
	public Json listView(Pageable pageable, String name, Boolean state, String creator, String place, @DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime) {
		Json j = new Json();

		try {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> geMap = new HashMap<String, Object>();
			Map<String, Object> leMap = new HashMap<String, Object>();
			map.put("state", state);
			map.put("creator", creator);
			map.put("place", place);
			geMap.put("startTime", startTime);
			leMap.put("startTime", endTime);

			List<Filter> filters = this.setFilterEqsAsMap(map);
			filters = this.addFilterGeMap(geMap, filters);
			filters = this.addFilterLeMap(leMap, filters);
//			filters.add(Filter.ge("startTime", startTime));
//			filters.add(Filter.le("endTime", endTime));
			filters.add(Filter.like("name", name));
			pageable.setFilters(filters);
			Page<Organization> page = this.findPage(pageable);

			Set<String> properties = new HashSet<String>() {
				{
					add("id");
					add("logoUrl");
					add("name");
					add("startTime");
					add("place");
					add("creator");
				}
			};
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = this.getResultByObjectMapper(page.getRows(),
					"Organization-Json-Filter", properties);

			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("result", result);
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(hm);

		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	//get organization basic information
	@Override
	public Json getDetail(Long organizationId) {
		Json j = new Json();
		try {
			
			Organization organization = this.find(organizationId);
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(organization);
			
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

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
	
	@Resource(name = "JournalServiceImpl")
	JournalService journalService;

	public static class WrapOrganization {
		private HashMap<String, Object> organization;
		private List<HashMap<String, Object>> activities = new ArrayList<>();
		private List<HashMap<String, Object>> journals = new ArrayList<>();
		private List<HashMap<String, Object>> literatures = new ArrayList<>();
		private List<RealObject> realObjects = new ArrayList<>();
		private List<Constitution> constitutions = new ArrayList<>();
		private List<HashMap<String, Object>> historicalDataIndexs = new ArrayList<>();
		
		public HashMap<String, Object> getOrganization() {
			return organization;
		}
		public void setOrganization(HashMap<String, Object> organization) {
			this.organization = organization;
		}
		public List<HashMap<String, Object>> getActivities() {
			return activities;
		}
		public void setActivities(List<HashMap<String, Object>> activities) {
			this.activities = activities;
		}
		public List<HashMap<String, Object>> getJournals() {
			return journals;
		}
		public void setJournals(List<HashMap<String, Object>> journals) {
			this.journals = journals;
		}
		public List<HashMap<String, Object>> getLiteratures() {
			return literatures;
		}
		public void setLiteratures(List<HashMap<String, Object>> literatures) {
			this.literatures = literatures;
		}
		public List<RealObject> getRealObjects() {
			return realObjects;
		}
		public void setRealObjects(List<RealObject> realObjects) {
			this.realObjects = realObjects;
		}
		public List<Constitution> getConstitutions() {
			return constitutions;
		}
		public void setConstitutions(List<Constitution> constitutions) {
			this.constitutions = constitutions;
		}
		public List<HashMap<String, Object>> getHistoricalDataIndexs() {
			return historicalDataIndexs;
		}
		public void setHistoricalDataIndexs(List<HashMap<String, Object>> historicalDataIndexs) {
			this.historicalDataIndexs = historicalDataIndexs;
		}
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
	public Json detailById(Long organizationId) {
		Json j = new Json();
		try {
			WrapOrganization wrapOrganization = new WrapOrganization();
			Organization organization = this.find(organizationId);
			
			
			ObjectMapper mapper = new ObjectMapper();
			FilterProvider myFilter = new SimpleFilterProvider().addFilter("Organization-Json-Filter",
					SimpleBeanPropertyFilter.serializeAllExcept());
			mapper.setFilters(myFilter);
			
			List<Filter> f = new ArrayList<Filter>();
			f.add(Filter.eq("entityId", organization.getId()));
			f.add(Filter.eq("type", 1));
			organization.setAudios(audioService.findList(null, f, null));
			organization.setVideos(videoService.getDetailByFilter(f));
			organization.setImages(imageService.getDetailByFilter(f));
			List<PeopleRelation> peopleRelations = peopleRelationService.findList(null, f, null);
			organization.setRelatedPeople(peopleRelationService.addPeopleName(peopleRelations));
			
			wrapOrganization.setOrganization(this.transferToHashMap(mapper, organization));
			
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("organizationId", organizationId));
			
			wrapOrganization.setJournals(journalService.getDetailByFilter(filters));
			wrapOrganization.setActivities(activityService.getDetailByFilter(filters));
			wrapOrganization.setConstitutions(constitutionService.getDetailByFilter(filters));
			wrapOrganization.setHistoricalDataIndexs(historicalDataIndexService.getDetailByFilter(filters));
			wrapOrganization.setLiteratures(literatureService.getDetailByFilter(filters));
			wrapOrganization.setRealObjects(realObjectService.getDetailByFilter(filters));

			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(wrapOrganization);
			
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	@Override
	public void addClickNumber(Long organizationId) {
		// TODO Auto-generated method stub
		Organization organization = this.find(organizationId);
		if(organization.getClickNumber() == null)
			organization.setClickNumber(1);
		else {
			organization.setClickNumber(organization.getClickNumber()+1);
		}
	}

}
