package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PositionDao;
import com.hongyu.entity.Position;
import com.hongyu.service.PositionService;

@Service("positionServiceImpl")
public class PositionServiceImpl extends BaseServiceImpl<Position,Long> implements PositionService {
	@Resource(name="positionDaoImpl")
	PositionDao positionDao;

	@Override
	public Position findByName(String name) {
		// TODO Auto-generated method stub
		return positionDao.findByName(name);
	}

	@Override
	@Resource(name="positionDaoImpl")
	public void setBaseDao(BaseDao<Position, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	
	
}
