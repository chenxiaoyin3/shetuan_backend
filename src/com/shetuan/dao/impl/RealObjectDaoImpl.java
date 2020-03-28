package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.RealObjectDao;
import com.shetuan.entity.RealObject;

@Repository("realObjectDaoImpl")
public class RealObjectDaoImpl extends BaseDaoImpl<RealObject,Long> implements RealObjectDao{
	
}