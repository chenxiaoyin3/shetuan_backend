package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.PeopleRelationDao;
import com.shetuan.entity.PeopleRelation;

@Repository("PeopleRelationDaoImpl")
public class PeopleRelationDaoImpl extends BaseDaoImpl<PeopleRelation,Long> implements PeopleRelationDao{

}

