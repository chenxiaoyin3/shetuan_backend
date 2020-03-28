package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.MhProductPictureDao;
import com.hongyu.entity.MhProductPicture;
import com.hongyu.service.MhProductPictureService;

@Service("mhProductPictureServiceImpl")
public class MhProductPictureServiceImpl extends BaseServiceImpl<MhProductPicture,Long> implements MhProductPictureService {
	@Resource(name = "mhProductPictureDaoImpl")
	MhProductPictureDao dao;
	
	@Resource(name = "mhProductPictureDaoImpl")
	public void setBaseDao(MhProductPictureDao dao){
		super.setBaseDao(dao);		
	}	
}
