package com.shetuan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.shetuan.entity.Activity;
import com.shetuan.entity.Audio;
import com.shetuan.entity.Image;
import com.shetuan.entity.Journal;
import com.shetuan.entity.Literature;
import com.shetuan.entity.Organization;
import com.shetuan.entity.People;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.entity.RealObject;
import com.shetuan.entity.Video;
import com.shetuan.service.ActivityService;
import com.shetuan.service.AudioService;
import com.shetuan.service.ImageService;
import com.shetuan.service.JournalService;
import com.shetuan.service.LiteratureService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.PeopleService;
import com.shetuan.service.PeopleTopicService;
import com.shetuan.service.RealObjectService;
import com.shetuan.service.VideoService;
@Service("PeopleTopicServiceImpl")
public class PeopleTopicServiceImpl implements PeopleTopicService{
	@Resource(name = "PeopleServiceImpl")
	PeopleService peopleService;
	
	@Resource(name = "PeopleRelationServiceImpl")
	PeopleRelationService peopleRelationService;
	
	@Resource(name = "OrganizationServiceImpl")
	OrganizationService organizationService;
	
	@Override
	public Json list(Pageable pageable, String name,String organizationName) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
//			按人物名称为主线检索
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			List<Filter> filter = new ArrayList<>();
//			获取人物名称
			if(name!=null) {
				filter.add(Filter.like("peopleName", name));
				filter.add(Filter.eq("state", true));
			}
			
			if(organizationName!=null) {
				List<People> peopleList=peopleService.findList(null, filter, null);
				int flag=1;
				for(People p:peopleList) {
					HashMap<String,Object> m=new HashMap<>();
					m.put("id", p.getId());
					m.put("name", p.getPeopleName());
					m.put("logoUrl", p.getLogoUrl());
					Long peopleId=p.getId();
					List<Filter> peopleRelationFilter=new ArrayList<>();
					peopleRelationFilter.add(Filter.eq("type", 1));
					peopleRelationFilter.add(Filter.eq("peopleId", peopleId));
					List<String> organizationNameList=new ArrayList<>();
					for(PeopleRelation pr:peopleRelationService.findList(null, peopleRelationFilter, null)) {
						Long organzationId=pr.getEntityId();
						organizationNameList.add(organizationService.find(organzationId).getName());
					}
					
					m.put("organizationName", organizationNameList);
					result.add(m);
					flag=0;//表示这个organizationNameList不保留
					for(String s:organizationNameList) {
						
						if(s.equals(organizationName)) {
							flag=2;//表示这个organizationNameList要保留
						}
					}
					if(flag!=2) {
						result.remove(m);
					}
					
				}
//				int start=pageable.getPage()*pageable.getRows()-pageable.getRows();
//				int end=pageable.getPage()*pageable.getRows()-1;
//				result=result.subList(start, end);
				hm.put("total",result.size());
				hm.put("pageNumber",pageable.getPage());
				hm.put("pageSize",pageable.getRows());
				hm.put("result",result);
			}
			else {
				pageable.setFilters(filter);
				Page<People> peoplePage=peopleService.findPage(pageable);
				for(People p:peoplePage.getRows()) {
					HashMap<String,Object> m=new HashMap<>();
					m.put("id", p.getId());
					m.put("name", p.getPeopleName());
					m.put("logoUrl", p.getLogoUrl());
					Long peopleId=p.getId();
					List<Filter> peopleRelationFilter=new ArrayList<>();
					peopleRelationFilter.add(Filter.eq("type", 1));
					peopleRelationFilter.add(Filter.eq("peopleId", peopleId));
					List<String> organizationNameList=new ArrayList<>();
					for(PeopleRelation pr:peopleRelationService.findList(null, peopleRelationFilter, null)) {
						Long organzationId=pr.getEntityId();
						organizationNameList.add(organizationService.find(organzationId).getName());
					}
					
					m.put("organizationName", organizationNameList);
					result.add(m);
				}
				hm.put("total",peoplePage.getTotal());
				hm.put("pageNumber",peoplePage.getPageNumber());
				hm.put("pageSize",peoplePage.getPageSize());
				hm.put("result",result);
			}
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(hm);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}
	
	@Resource(name = "RealObjectServiceImpl")
	RealObjectService realObjectService;
	
	@Resource(name = "JournalServiceImpl")
	JournalService journalService;

	@Resource(name = "ActivityServiceImpl")
	ActivityService activityService;

	@Resource(name = "LiteratureServiceImpl")
	LiteratureService literatureService;
	
	@Resource(name = "AudioServiceImpl")
	AudioService audioService;
	
	@Resource(name = "VideoServiceImpl")
	VideoService videoService;

	@Resource(name = "ImageServiceImpl")
	ImageService imageService;
	
	@Override
	public Json detailById(Long id) {
		Json j = new Json();
		try {
			People people = peopleService.find(id);
			if(people == null || people.getState() == false || people.getDescription() == null) {
				j.setSuccess(true);
				j.setMsg("人物无效");
				j.setObj(null);
			}
			else {
				HashMap<String, Object> result = new HashMap<String, Object>();
				result.put("peopleName",people.getPeopleName());
				result.put("logoUrl",people.getLogoUrl());
				
				List<Filter> filter = new ArrayList<>();
				filter.add(Filter.eq("peopleId", id));
				List<PeopleRelation> peopleRelations = peopleRelationService.findList(null, filter, null);
				
				List<HashMap<String, Object>> organizations = new ArrayList<HashMap<String,Object>>();
				List<HashMap<String, Object>> activities = new ArrayList<HashMap<String,Object>>();
				List<HashMap<String, Object>> journals = new ArrayList<HashMap<String,Object>>();
				List<HashMap<String, Object>> literatures = new ArrayList<HashMap<String,Object>>();
				List<HashMap<String, Object>> realObjects = new ArrayList<HashMap<String,Object>>();
				List<HashMap<String, Object>> images = new ArrayList<HashMap<String,Object>>();
				List<HashMap<String, Object>> videos = new ArrayList<HashMap<String,Object>>();
				List<HashMap<String, Object>> audios = new ArrayList<HashMap<String,Object>>();
				
				for(PeopleRelation peopleRelation : peopleRelations) {
					if(peopleRelation.getType() == Integer.valueOf(1)) {
						Organization organization = organizationService.find(peopleRelation.getEntityId());
						HashMap<String, Object> mp = new HashMap<String, Object>();
						mp.put("id", organization.getId());
						mp.put("name", organization.getName());
						mp.put("startTime", organization.getStartTime());
						mp.put("creator", organization.getCreator());
						mp.put("member", organization.getMember());
						mp.put("leader", organization.getLeader());
						mp.put("secretariat", organization.getSecretariat());
						organizations.add(mp);
					}
					else if(peopleRelation.getType() == Integer.valueOf(2)) {
						Activity activity = activityService.find(peopleRelation.getEntityId());
						HashMap<String, Object> mp = new HashMap<String, Object>();
						mp.put("id", activity.getId());
						mp.put("name", activity.getName());
						mp.put("startTime", activity.getStartTime());
						mp.put("relationDescription", peopleRelation.getRelationDescription());
						activities.add(mp);
					}
					else if(peopleRelation.getType() == Integer.valueOf(3)) {
						Journal journal = journalService.find(peopleRelation.getEntityId());
						HashMap<String, Object> mp = new HashMap<String, Object>();
						mp.put("id", journal.getId());
						mp.put("name", journal.getName());
						mp.put("startTime", journal.getStartTime());
						mp.put("relationDescription", peopleRelation.getRelationDescription());
						journals.add(mp);
					}
					else if(peopleRelation.getType() == Integer.valueOf(4)) {
						Literature literature = literatureService.find(peopleRelation.getEntityId());
						HashMap<String, Object> mp = new HashMap<String, Object>();
						mp.put("id", literature.getId());
						mp.put("name", literature.getName());
						mp.put("startTime", literature.getCreateTime());
						mp.put("relationDescription", peopleRelation.getRelationDescription());
						literatures.add(mp);
					}
					else if(peopleRelation.getType() == Integer.valueOf(5)) {
						RealObject realObject = realObjectService.find(peopleRelation.getEntityId());
						HashMap<String, Object> mp = new HashMap<String, Object>();
						mp.put("id", realObject.getId());
						mp.put("name", realObject.getName());
						mp.put("startTime", realObject.getCreateTime());
						mp.put("description", realObject.getDescription());
						mp.put("relationDescription", peopleRelation.getRelationDescription());
						realObjects.add(mp);
					}
					else if(peopleRelation.getType() == Integer.valueOf(6)) {
						Image image = imageService.find(peopleRelation.getEntityId());
						HashMap<String, Object> mp = new HashMap<String, Object>();
						mp.put("id", image.getId());
						mp.put("name", image.getName());
						mp.put("description", image.getDescription());
						mp.put("url", image.getUrl());
						images.add(mp);
					}
					else if(peopleRelation.getType() == Integer.valueOf(7)) {
						Audio audio = audioService.find(peopleRelation.getEntityId());
						HashMap<String, Object> mp = new HashMap<String, Object>();
						mp.put("id", audio.getId());
						mp.put("name", audio.getName());
						mp.put("description", audio.getDescription());
						audios.add(mp);
					} 
					else if(peopleRelation.getType() == Integer.valueOf(8)) {
						Video video = videoService.find(peopleRelation.getEntityId());
						HashMap<String, Object> mp = new HashMap<String, Object>();
						mp.put("id", video.getId());
						mp.put("name", video.getName());
						mp.put("description", video.getDescription());
						mp.put("url", video.getUrl());
						videos.add(mp);
					}
				}
				result.put("organizations",organizations);
				result.put("activities",activities);
				result.put("journals",journals);
				result.put("literatures", literatures);
				result.put("realObjects", realObjects);
				result.put("images", images);
				result.put("videos", videos);
				result.put("audios", audios);
								
				j.setSuccess(true);
				j.setMsg("获取成功");
				j.setObj(result);	
			}
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("获取失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

}
