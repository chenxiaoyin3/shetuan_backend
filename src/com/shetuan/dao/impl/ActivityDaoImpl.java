package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.ActivityDao;
import com.shetuan.entity.Activity;

@Repository("ActivityDaoImpl")
public class ActivityDaoImpl extends BaseDaoImpl<Activity,Long> implements ActivityDao{

}
