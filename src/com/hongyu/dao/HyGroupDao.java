package com.hongyu.dao;

import java.util.Date;
import java.util.List;

import com.grain.dao.BaseDao;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;

public interface HyGroupDao extends BaseDao<HyGroup, Long> {
	List<Date> groupDateExist(Long line, Long id,Boolean teamType);
}
