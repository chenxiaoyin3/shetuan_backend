package com.shetuan.service.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.shetuan.entity.Constitution;
import com.shetuan.service.AudioService;
import com.shetuan.service.ConstitutionService;
import com.shetuan.service.ImageService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.VideoService;
@Service("ConstitutionServiceImpl")
public class ConstitutionServiceImpl extends BaseServiceImpl<Constitution,Long> implements ConstitutionService{
	@Override
	@Resource(name="ConstitutionDaoImpl")
	public void setBaseDao(BaseDao<Constitution,Long> baseDao) {
		super.setBaseDao(baseDao);
	}

	@Resource(name="OrganizationServiceImpl")
	OrganizationService organizationService;

	@Resource(name = "AudioServiceImpl")
	AudioService audioService;
	
	@Resource(name = "VideoServiceImpl")
	VideoService videoService;

	@Resource(name = "ImageServiceImpl")
	ImageService imageService;

	@Override
	public List<Constitution> getDetailByFilter(List<Filter> filters) {
		List<Constitution> constitutions = this.findList(null, filters, null);
		
		for(Constitution constitution : constitutions) {
			List<Filter> constitutionFilter = new ArrayList<Filter>();
			constitutionFilter.add(Filter.eq("entityId", constitution.getId()));
			constitutionFilter.add(Filter.eq("type", 6));
			//constitution.setAudios(audioService.findList(null, constitutionFilter, null));
			//constitution.setVideos(videoService.getDetailByFilter(constitutionFilter));
			constitution.setImages(imageService.getDetailAndPeopleByFilter(constitutionFilter));
		}
		return constitutions;
	}
}
