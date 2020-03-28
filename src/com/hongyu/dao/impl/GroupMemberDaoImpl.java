package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GroupMemberDao;
import com.hongyu.entity.GroupMember;

@Repository("groupMemberDaoImpl")
public class GroupMemberDaoImpl extends BaseDaoImpl<GroupMember, Long> implements GroupMemberDao{

}
