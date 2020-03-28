package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.GroupBiankoudianDao;
import com.hongyu.entity.GroupBiankoudian;
import com.hongyu.service.GroupBiankoudianService;
@Service("groupBiankoudianServiceImpl")
public class GroupBiankoudianServiceImpl extends BaseServiceImpl<GroupBiankoudian, Long>
		implements GroupBiankoudianService {
	@Resource(name = "groupBiankoudianDaoImpl")
	GroupBiankoudianDao dao;

	@Resource(name = "groupBiankoudianDaoImpl")
	public void setBaseDao(GroupBiankoudianDao dao) {
		super.setBaseDao(dao);
	}
}
