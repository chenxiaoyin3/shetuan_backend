package com.hongyu.dao;

import com.grain.dao.BaseDao;
import com.hongyu.entity.Position;

public interface PositionDao extends BaseDao<Position, Long> {
	public Position findByName(String name);
}
