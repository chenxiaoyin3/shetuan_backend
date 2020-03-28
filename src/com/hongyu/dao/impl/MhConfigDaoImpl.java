package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MhConfigDao;
import com.hongyu.entity.MhConfig;

@Repository("mhConfigDaoImpl")
public class MhConfigDaoImpl extends BaseDaoImpl<MhConfig, Long> implements MhConfigDao{
	
}
