package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.TouristInfoDao;
import com.hongyu.entity.TouristInfo;

@Repository("touristInfoDaoImpl")
public class TouristInfoDaoImpl extends BaseDaoImpl<TouristInfo, Long> implements TouristInfoDao {

}
