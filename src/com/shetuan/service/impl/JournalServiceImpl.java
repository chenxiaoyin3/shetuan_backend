package com.shetuan.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.Filter;
import com.shetuan.entity.Activity;
import com.shetuan.entity.Audio;
import com.shetuan.entity.Journal;
import com.shetuan.entity.Organization;
import com.shetuan.entity.People;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.service.AudioService;
import com.shetuan.service.ImageService;
import com.shetuan.service.JournalService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.VideoService;
@Service("JournalServiceImpl")
public class JournalServiceImpl extends BaseServiceImpl<Journal,Long> implements JournalService{
	@Override
	@Resource(name="JournalDaoImpl")
	public void setBaseDao(BaseDao<Journal,Long> baseDao) {
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

	@Resource(name = "OrganizationServiceImpl")
	OrganizationService organizationService;

//	@Override
//	public List<Journal> getDetailByFilter(List<Filter> filters) {
//		List<Journal> journals = this.findList(null, filters, null);
//		
//		for(Journal journal : journals) {
//			List<Filter> filter = new ArrayList<Filter>();
//			filters.add(Filter.eq("entityId", journal.getId()));
//			filters.add(Filter.eq("type", 3));
//			journal.setAudios(audioService.findList(null, filter, null));
//			journal.setVideos(videoService.findList(null, filter, null));
//			journal.setImages(imageService.findList(null, filter, null));
//			journal.setRelatedPeople(peopleRelationService.findList(null, filter, null));
//		}
//		return journals;
//	}

	@Override
	public List<HashMap<String, Object>> getDetailByFilter(List<Filter> filters) {
		List<Journal> journals = this.findList(null, filters, null);
		List<HashMap<String, Object>> result=new ArrayList<HashMap<String,Object>>();
		
		for(Journal journal : journals) {
			HashMap<String, Object> m = objectToMap(journal);
			
			List<Filter> f = new ArrayList<Filter>();
			f.add(Filter.eq("entityId", journal.getId()));
			f.add(Filter.eq("type", 3));
			
//			List<Audio> audio = audioService.getDetailAndPeopleByFilter(f);
//			List<HashMap<String, Object>> audios = new ArrayList<HashMap<String,Object>>();
//			for(Audio audio2 : audio) {
//				audios.add(objectToMap(audio2));
//			}
//			m.put("audios", audios);
//			
//			m.put("videos", videoService.getDetailAndPeopleByFilter(f));
			m.put("images", imageService.getDetailAndPeopleByFilter(f));
			
			List<PeopleRelation> peopleRelations = peopleRelationService.findList(null, f, null);
			peopleRelations = peopleRelationService.addPeopleName(peopleRelations);
			List<HashMap<String, Object>> relatedPeople = new ArrayList<HashMap<String,Object>>();
			for(PeopleRelation peopleRelation : peopleRelations) {
				relatedPeople.add(objectToMap(peopleRelation));
			}
			m.put("relatedPeople", relatedPeople);
			result.add(m);
		}
		
		return result;
	}
	
	public static HashMap<String, Object> objectToMap(Object obj) { 
    	HashMap<String, Object> params = new HashMap<String, Object>(0); 
            try { 
                PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean(); 
                PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj); 
                for (int i = 0; i < descriptors.length; i++) { 
                    String name = descriptors[i].getName(); 
                    if (!"class".equals(name)) { 
                        params.put(name, propertyUtilsBean.getNestedProperty(obj, name)); 
                    } 
                } 
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
            return params; 
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

