package com.shetuan.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.shetuan.entity.Image;
import com.shetuan.entity.Literature;
import com.shetuan.entity.Organization;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.service.AudioService;
import com.shetuan.service.ImageService;
import com.shetuan.service.LiteratureService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.VideoService;
@Service("LiteratureServiceImpl")
public class LiteratureServiceImpl extends BaseServiceImpl<Literature,Long> implements LiteratureService{
	@Override
	@Resource(name="LiteratureDaoImpl")
	public void setBaseDao(BaseDao<Literature,Long> baseDao) {
		super.setBaseDao(baseDao);
	}

	@Resource(name="OrganizationServiceImpl")
	OrganizationService organizationService;
	
	@Resource(name="ImageServiceImpl")
	ImageService imageService;

	@Resource(name = "AudioServiceImpl")
	AudioService audioService;
	
	@Resource(name = "VideoServiceImpl")
	VideoService videoService;
	
	@Resource(name = "PeopleRelationServiceImpl")
	PeopleRelationService peopleRelationService;
	
	@Override
	public Json listView(Pageable pageable, String name, String organizationName, Integer type, @DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime) {
		Json j = new Json();
		
		try {
			Map<String, Object> eqMap = new HashMap<String, Object>();
			Map<String, Object> geMap = new HashMap<String, Object>();
			Map<String, Object> leMap = new HashMap<String, Object>();
			
			eqMap.put("type", type);
			geMap.put("createTime", startTime);
			leMap.put("createTime", endTime);
			
			List<Filter> filters = this.setFilterEqsAsMap(eqMap);
			filters = this.addFilterGeMap(geMap, filters);
			filters = this.addFilterLeMap(leMap, filters);

			if(null != name)
				filters.add(Filter.like("name", name));
			
			if(null != organizationName) {
				List<Long> organizationIds = new ArrayList<Long>();
				List<Filter> f = new ArrayList<Filter>();
				f.add(Filter.like("name", organizationName));
				List<Organization> organizations = organizationService.findList(null, f, null);
				for(Organization organization : organizations) {
					organizationIds.add(organization.getId());
				}
				if(organizationIds.isEmpty()) {
					organizationIds.add((long) 0);				
				}
				filters.add(Filter.in("organizationId", organizationIds));
			}
			
			pageable.setFilters(filters);
			Page<Literature> page = this.findPage(pageable);

			for(Literature literature : page.getRows()) {
				Long organizationId=literature.getOrganizationId();
				literature.setOrganizationName(organizationService.find(organizationId).getName());
				List<Filter> imageFilters=new ArrayList<Filter>();
				imageFilters.add(Filter.eq("type", 4));//type = literature
				imageFilters.add(Filter.eq("entityId", literature.getId()));
				List<HashMap<String, Object>> image = imageService.getDetailByFilter(imageFilters);
				if(image.isEmpty())
					literature.setImage(null);
				else
					literature.setImage(image.get(0).get("url").toString());
			}
			
			Set<String> properties = new HashSet<String>() {{
				add("id");
				add("organizationName");
				add("name");
				add("createTime");
				add("image");
			}};
			HashMap<String,Object> hm=new HashMap<String,Object>();
			List<HashMap<String,Object>> result=
					this.getResultByObjectMapper(page.getRows(), "Literature-Json-Filter",properties);
			
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
	public List<HashMap<String, Object>> getDetailByFilter(List<Filter> filters) {
		List<Literature> literatures = this.findList(null, filters, null);
		
		for(Literature literature : literatures) {
			List<Filter> f = new ArrayList<Filter>();
			f.add(Filter.eq("entityId", literature.getId()));
			f.add(Filter.eq("type", 4));
			//literature.setAudios(audioService.getDetailAndPeopleByFilter(f));
			//literature.setVideos(videoService.getDetailAndPeopleByFilter(f));
			literature.setImages(imageService.getDetailAndPeopleByFilter(f));
			List<PeopleRelation> peopleRelations = peopleRelationService.findList(null, f, null);
			literature.setRelatedPeople(peopleRelationService.addPeopleName(peopleRelations));
		}

		ObjectMapper mapper = new ObjectMapper();
		FilterProvider myFilter = new SimpleFilterProvider().addFilter("Literature-Json-Filter",
				SimpleBeanPropertyFilter.serializeAllExcept());
		mapper.setFilters(myFilter);
		
		List<HashMap<String, Object>> result=this.transferToHashMapList(mapper, literatures);
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
