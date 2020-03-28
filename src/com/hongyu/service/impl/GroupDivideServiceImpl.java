package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.GroupDivideDao;
import com.hongyu.entity.GroupDivide;
import com.hongyu.service.GroupDivideService;


@Service("groupDivideServiceImpl")
public class GroupDivideServiceImpl extends BaseServiceImpl<GroupDivide, Long> implements GroupDivideService{
	@Resource(name = "groupDivideDaoImpl")
	GroupDivideDao dao;
	
	@Resource(name = "groupDivideDaoImpl")
	public void setBaseDao(GroupDivideDao dao){
		super.setBaseDao(dao);		
	}	
}
