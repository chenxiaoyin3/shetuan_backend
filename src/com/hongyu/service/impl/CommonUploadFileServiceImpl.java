package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CommonUploadFileDao;
import com.hongyu.entity.CommonUploadFileEntity;
import com.hongyu.service.CommonUploadFileService;
@Service(value = "commonUploadFileServiceImpl")
public class CommonUploadFileServiceImpl extends BaseServiceImpl<CommonUploadFileEntity, Long>
		implements CommonUploadFileService {
	@Resource(name = "commonUploadFileDaoImpl")
	CommonUploadFileDao dao;
	
	@Resource(name = "commonUploadFileDaoImpl")
	public void setBaseDao(CommonUploadFileDao dao){
		super.setBaseDao(dao);		
	}
}
