package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.PeopleDao;
import com.shetuan.entity.People;

@Repository("PeopleDaoImpl")
public class PeopleDaoImpl extends BaseDaoImpl<People,Long> implements PeopleDao{

}

