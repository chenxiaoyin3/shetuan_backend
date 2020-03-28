package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.ExperienceGroup;
import com.hongyu.service.ExperienceGroupService;

@Service("experienceGroupServiceImpl")
public class ExperienceGroupServiceImpl extends BaseServiceImpl<ExperienceGroup, Long> implements ExperienceGroupService {

	@Override
	@Resource(name="experienceGroupDaoImpl")
	public void setBaseDao(BaseDao<ExperienceGroup, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
