package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.ConstitutionDao;
import com.shetuan.entity.Constitution;

@Repository("ConstitutionDaoImpl")
public class ConstitutionDaoImpl extends BaseDaoImpl<Constitution,Long> implements ConstitutionDao{
	
}