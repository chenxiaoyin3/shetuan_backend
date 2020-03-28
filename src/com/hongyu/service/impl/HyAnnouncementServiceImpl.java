package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyAnnouncementDao;
import com.hongyu.entity.HyAnnouncement;
import com.hongyu.service.HyAnnouncementService;

@Service(value = "hyAnnouncementServiceImpl")
public class HyAnnouncementServiceImpl extends BaseServiceImpl<HyAnnouncement, Long> implements HyAnnouncementService{
	@Resource(name = "hyAnnouncementDaoImpl")
	HyAnnouncementDao hyAnnouncementDao;
	
	@Resource(name = "hyAnnouncementDaoImpl")
	public void setBaseDao(HyAnnouncementDao dao){
		super.setBaseDao(dao);		
	}
}
