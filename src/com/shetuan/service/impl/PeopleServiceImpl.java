package com.shetuan.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.shetuan.entity.People;
import com.shetuan.service.PeopleService;
@Service("PeopleServiceImpl")
public class PeopleServiceImpl extends BaseServiceImpl<People,Long> implements PeopleService{
	@Override
	@Resource(name="PeopleDaoImpl")
	public void setBaseDao(BaseDao<People,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
}


