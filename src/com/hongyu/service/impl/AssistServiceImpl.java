package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.AssistDao;
import com.hongyu.entity.Assist;
import com.hongyu.service.AssistService;

@Service("assistServiceImpl")
public class AssistServiceImpl extends BaseServiceImpl<Assist, Long> implements AssistService {

	@Resource(name="assistDaoImpl")
	AssistDao assistDao;

	
	@Override
	@Resource(name="assistDaoImpl")
	public void setBaseDao(BaseDao<Assist, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
	
	
}
