package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.GroupMemberDao;
import com.hongyu.entity.GroupMember;
import com.hongyu.service.GroupMemberService;

@Service("groupMemberServiceImpl")
public class GroupMemberServiceImpl extends BaseServiceImpl<GroupMember, Long> implements GroupMemberService{
	@Resource(name = "groupMemberDaoImpl")
	GroupMemberDao dao;
	
	@Resource(name = "groupMemberDaoImpl")
	public void setBaseDao(GroupMemberDao dao){
		super.setBaseDao(dao);	
	}
}
