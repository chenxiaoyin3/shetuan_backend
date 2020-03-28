package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.GroupXiaotuanDao;
import com.hongyu.entity.GroupXiaotuan;
import com.hongyu.service.GroupXiaotuanService;
@Service(value = "groupXiaotuanServiceImpl")
public class GroupXiaotuanServiceImpl extends BaseServiceImpl<GroupXiaotuan, Long> implements GroupXiaotuanService {
	@Resource(name = "groupXiaotuanDaoImpl")
	GroupXiaotuanDao dao;
	
	@Resource(name = "groupXiaotuanDaoImpl")
	public void setBaseDao(GroupXiaotuanDao dao){
		super.setBaseDao(dao);		
	}	
}
