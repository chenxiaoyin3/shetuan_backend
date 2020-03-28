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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.shetuan.entity.HistoricalDataIndex;
import com.shetuan.entity.Image;
import com.shetuan.entity.Journal;
import com.shetuan.entity.Literature;
import com.shetuan.entity.Organization;
import com.shetuan.service.AudioService;
import com.shetuan.service.HistoricalDataIndexService;
import com.shetuan.service.ImageService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.VideoService;

import oracle.net.aso.f;
@Service("HistoricalDataIndexServiceImpl")
public class HistoricalDataIndexServiceImpl extends BaseServiceImpl<HistoricalDataIndex,Long> implements HistoricalDataIndexService{
	@Override
	@Resource(name="HistoricalDataIndexDaoImpl")
	public void setBaseDao(BaseDao<HistoricalDataIndex,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
	@Resource(name="OrganizationServiceImpl")
	OrganizationService organizationService;
	
	@Resource(name="ImageServiceImpl")
	ImageService imageService;

	@Override
	public Json listView(Pageable pageable, String name, String organizationName, @DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime) {
		Json j = new Json();
		
		try {
			Map<String, Object> eqMap = new HashMap<String, Object>();
			Map<String, Object> geMap = new HashMap<String, Object>();
			Map<String, Object> leMap = new HashMap<String, Object>();
			
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
				if(organizationIds.isEmpty())
					organizationIds.add((long) 0);
				filters.add(Filter.in("organizationId", organizationIds));
			}
			
			pageable.setFilters(filters);
			Page<HistoricalDataIndex> page = this.findPage(pageable);

			for(HistoricalDataIndex historical : page.getRows()) {
				Long organizationId=historical.getOrganizationId();
				historical.setOrganizationName(organizationService.find(organizationId).getName());
				List<Filter> imageFilters=new ArrayList<Filter>();
				imageFilters.add(Filter.eq("type", 7));//type = HistoricalDataIndex
				imageFilters.add(Filter.eq("entityId", historical.getId()));
				List<HashMap<String, Object>> image = imageService.getDetailByFilter(imageFilters);
				if(image.isEmpty())
					historical.setImage(null);
				else
					historical.setImage(image.get(0).get("url").toString());
			}
			
			Set<String> properties = new HashSet<String>() {{
				add("id");
				add("organizationName");
				add("name");
				add("image");
				add("createTime");
			}};
			HashMap<String,Object> hm=new HashMap<String,Object>();
			List<HashMap<String,Object>> result=
					this.getResultByObjectMapper(page.getRows(), "HistoricalDataIndex-Json-Filter",properties);
			
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
	
	@Override
	public List<HashMap<String, Object>> getDetailByFilter(List<Filter> filters) {
		
		List<HistoricalDataIndex> historicalDataIndexs = this.findList(null, filters, null);
		
		for(HistoricalDataIndex historicalDataIndex : historicalDataIndexs) {
			List<Filter> f = new ArrayList<Filter>();
			f.add(Filter.eq("entityId", historicalDataIndex.getId()));
			f.add(Filter.eq("type", 7));
			//historicalDataIndex.setAudios(audioService.findList(null, f, null));
			//historicalDataIndex.setVideos(videoService.getDetailByFilter(f));
			historicalDataIndex.setImages(imageService.getDetailAndPeopleByFilter(f));
		}
		
		ObjectMapper mapper = new ObjectMapper();
		FilterProvider myFilter = new SimpleFilterProvider().addFilter("HistoricalDataIndex-Json-Filter",
				SimpleBeanPropertyFilter.serializeAllExcept());
		mapper.setFilters(myFilter);
		
		List<HashMap<String, Object>> result=this.transferToHashMapList(mapper, historicalDataIndexs);
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

