package com.shetuan.service.impl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.shetuan.entity.Activity;
import com.shetuan.entity.Audio;
import com.shetuan.entity.Image;
import com.shetuan.entity.Journal;
import com.shetuan.entity.Organization;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.service.AudioService;
import com.shetuan.service.ImageService;
import com.shetuan.service.JournalService;
import com.shetuan.service.JournalTopicService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.VideoService;

import oracle.net.aso.a;

@Service("JournalTopicServiceImpl")
public class JournalTopicServiceImpl implements JournalTopicService {
	@Resource(name = "OrganizationServiceImpl")
	OrganizationService organizationService;

	@Resource(name = "JournalServiceImpl")
	JournalService journalService;
	
	@Resource(name = "ImageServiceImpl")
	ImageService imageService;

	@Override
	public Json list(Pageable pageable, String name, String organizationName, @DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime,
			boolean state) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			List<Filter> filter = new ArrayList<>();
			if (organizationName != null) {
//				Organization organization=organizationService.find(organizationId);
//				filter.add(Filter.eq("organizationId", organizationId));
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
				filter.add(Filter.in("organizationId", organizationIds));
			}
			if (name != null) {
				filter.add(Filter.like("name", name));
			}
			if (startTime != null && endTime != null) {
				filter.add(Filter.ge("startTime", startTime));
				filter.add(Filter.le("startTime", endTime));
			}
			filter.add(Filter.like("state", state));
			pageable.setFilters(filter);
			Page<Journal> page=journalService.findPage(pageable);
			filter.clear();
			List<Filter> imageFilter=new ArrayList<>();
			for(Journal tmp:page.getRows()) {
				HashMap<String,Object> m=new HashMap<String,Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				Long organizationId=tmp.getOrganizationId();
				m.put("organizationName", organizationService.find(organizationId).getName());
				m.put("startTime", tmp.getStartTime());
				m.put("endTime", tmp.getEndTime());
				imageFilter.add(Filter.eq("type", 3));
				imageFilter.add(Filter.eq("entityId", tmp.getId()));
				List<Image> listImage=imageService.findList(null, imageFilter, null);
				imageFilter.clear();
				if(!listImage.isEmpty()) {
					m.put("image", listImage.get(0).getUrl());
				}
				result.add(m);
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
			j.setMsg("获取失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}
}
