package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GroupSendGuideDao;
import com.hongyu.entity.GroupSendGuide;
@Repository("groupSendGuideDaoImpl")
public class GroupSendGuideDaoImpl extends BaseDaoImpl<GroupSendGuide, Long> implements GroupSendGuideDao {

}
