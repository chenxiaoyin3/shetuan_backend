package com.shetuan.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.FddContract;
import com.hongyu.util.contract.SelfPayAgreement;
import com.hongyu.util.contract.ShoppingAgreement;
import com.shetuan.entity.Audio;
import com.shetuan.entity.Image;
import com.shetuan.entity.Journal;
import com.shetuan.entity.Literature;
import com.shetuan.entity.Organization;
import com.shetuan.entity.People;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.entity.RealObject;
import com.shetuan.entity.Video;
import com.shetuan.service.AudioService;
import com.shetuan.service.ImageService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.OrganizationTopicService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.PeopleService;
import com.shetuan.service.RealObjectService;
import com.shetuan.service.VideoService;

@Service("OrganizationTopicServiceImpl")
public class OrganizationTopicServiceImpl implements OrganizationTopicService {
	@Resource(name = "OrganizationServiceImpl")
	OrganizationService organizationService;

	@Resource(name = "RealObjectServiceImpl")
	RealObjectService realObjectService;

	@Resource(name = "ImageServiceImpl")
	ImageService imageService;

	@Resource(name = "AudioServiceImpl")
	AudioService audioService;

	@Resource(name = "VideoServiceImpl")
	VideoService videoService;

	@Resource(name = "PeopleRelationServiceImpl")
	PeopleRelationService peopleRelationService;

	@Resource(name = "PeopleServiceImpl")
	PeopleService peopleService;

	public static class ImageWrap {
		Image image;
		List<People> relatedPeople;

		public Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			this.image = image;
		}

		public List<People> getRelatedPeople() {
			return relatedPeople;
		}

		public void setRelatedPeople(List<People> relatedPeople) {
			this.relatedPeople = relatedPeople;
		}

	}

	@SuppressWarnings("serial")
	@Override
	public Json listView(Pageable pageable, String name, Boolean state, String creator, String place, Date startTime,
			Date endTime) {
		Json j = new Json();

		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", name);
			map.put("state", state);
			map.put("creator", creator);
			map.put("place", place);
			map.put("startTime", startTime);
			map.put("endTime", endTime);

			List<Filter> filters = organizationService.setFilterEqsAsMap(map);
			pageable.setFilters(filters);
			Page<Organization> page = organizationService.findPage(pageable);

			Set<String> properties = new HashSet<String>() {
				{
					add("logoUrl");
					add("name");
					add("startTime");
					add("place");
					add("creator");
				}
			};
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = organizationService.getResultByObjectMapper(page.getRows(),
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

	@Override
	public Json realObjectDetailById(Long id) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("id", id));
			List<RealObject> result=realObjectService.getDetailByFilter(filters);
			HashMap<String,Object> m=new HashMap<>();
			if(!result.isEmpty()) {
				m.put("realObjectDetail", result.get(0));
				Organization o=organizationService.find(realObjectService.find(id).getOrganizationId());
				m.put("organizationName", o.getName());
			}
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(m);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

}
