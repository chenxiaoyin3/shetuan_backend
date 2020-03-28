package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GuideAssignmentDao;
import com.hongyu.entity.GuideAssignment;

@Repository("guideAssignmentDaoImpl")
public class GuideAssignmentDaoImpl extends BaseDaoImpl<GuideAssignment, Long> implements GuideAssignmentDao{

}
