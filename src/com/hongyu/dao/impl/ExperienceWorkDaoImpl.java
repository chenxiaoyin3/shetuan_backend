package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ExperienceWorkDao;
import com.hongyu.entity.ExperienceWork;

@Repository("experienceWorkDaoImpl")
public class ExperienceWorkDaoImpl extends BaseDaoImpl<ExperienceWork, Long> implements ExperienceWorkDao {

}
