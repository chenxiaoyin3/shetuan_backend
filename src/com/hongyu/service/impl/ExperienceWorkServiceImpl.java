package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.ExperienceWork;
import com.hongyu.service.ExperienceWorkService;

@Service("experienceWorkServiceImpl")
public class ExperienceWorkServiceImpl extends BaseServiceImpl<ExperienceWork, Long> implements ExperienceWorkService {

	@Override
	@Resource(name="experienceWorkDaoImpl")
	public void setBaseDao(BaseDao<ExperienceWork, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
