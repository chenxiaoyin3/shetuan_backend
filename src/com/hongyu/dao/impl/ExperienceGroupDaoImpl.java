package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ExperienceGroupDao;
import com.hongyu.entity.ExperienceGroup;

@Repository("experienceGroupDaoImpl")
public class ExperienceGroupDaoImpl extends BaseDaoImpl<ExperienceGroup, Long> implements ExperienceGroupDao {

}
