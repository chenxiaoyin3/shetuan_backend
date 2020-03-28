package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyDepartmentModelDao;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.service.HyDepartmentModelService;
@Service(value = "hyDepartmentModelServiceImpl")
public class HyDepartmentModelServiceImpl extends BaseServiceImpl<HyDepartmentModel, String>
		implements HyDepartmentModelService {
	@Resource(name = "hyDepartmentModelDaoImpl")
	HyDepartmentModelDao dao;
	
	@Resource(name = "hyDepartmentModelDaoImpl")
	public void setBaseDao(HyDepartmentModelDao dao){
		super.setBaseDao(dao);		
	}

}
