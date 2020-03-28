package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.RealObjectSourceDao;
import com.shetuan.entity.RealObjectSource;

@Repository("realObjectSourceDaoImpl")
public class RealObjectSourceDaoImpl extends BaseDaoImpl<RealObjectSource,Long> implements RealObjectSourceDao{
	
}