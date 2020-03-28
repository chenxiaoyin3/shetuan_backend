package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.GroupStoreCancelDao;
import com.hongyu.entity.GroupStoreCancel;
import com.hongyu.service.GroupStoreCancelService;
@Service(value = "groupStoreCancelServiceImpl")
public class GroupStoreCancelServiceImpl extends BaseServiceImpl<GroupStoreCancel, Long>
		implements GroupStoreCancelService {
	@Resource(name = "groupStoreCancelDaoImpl")
	GroupStoreCancelDao dao;
	
	@Resource(name = "groupStoreCancelDaoImpl")
	public void setBaseDao(GroupStoreCancelDao dao){
		super.setBaseDao(dao);		
	}	

}
