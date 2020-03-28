package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.AdministrativeUploadDao;
import com.hongyu.entity.AdministrativeUpload;
import com.hongyu.service.AdministrativeUploadService;

@Service("administrativeUploadServiceImpl")
public class AdministrativeUploadServiceImpl extends BaseServiceImpl<AdministrativeUpload, Long> implements AdministrativeUploadService{
	@Resource(name="administrativeUploadDaoImpl")
	AdministrativeUploadDao administrativeUploadDaoImpl;
	  
	@Resource(name="administrativeUploadDaoImpl")
	public void setBaseDao(AdministrativeUploadDao dao)
	{
	    super.setBaseDao(dao);
	}
}