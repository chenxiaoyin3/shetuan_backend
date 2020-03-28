package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.Position;

public interface PositionService extends BaseService<Position, Long> {
	public Position findByName(String name);
}
