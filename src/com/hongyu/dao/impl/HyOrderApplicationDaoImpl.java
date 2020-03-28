package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyOrderApplicationDao;
import com.hongyu.entity.HyOrderApplication;

@Repository("hyOrderApplicationDaoImpl")
public class HyOrderApplicationDaoImpl extends BaseDaoImpl<HyOrderApplication, Long> implements HyOrderApplicationDao{

}
