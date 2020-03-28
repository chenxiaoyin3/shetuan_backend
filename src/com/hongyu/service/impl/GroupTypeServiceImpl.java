package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.GroupTypeDao;
import com.hongyu.entity.GroupType;
import com.hongyu.service.GroupTypeService;
@Service(value = "groupTypeServiceImpl")
public class GroupTypeServiceImpl extends BaseServiceImpl<GroupType, Long> 
implements GroupTypeService {

	
	@Resource(name = "groupTypeDaoImpl")
	GroupTypeDao dao;
	
	@Resource(name = "groupTypeDaoImpl")
	public void setBaseDao(GroupTypeDao dao){
		super.setBaseDao(dao);		
	}	
	
}
