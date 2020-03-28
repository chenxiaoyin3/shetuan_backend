package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.TrafficTypeDao;
import com.hongyu.entity.TrafficType;
@Repository("trafficTypeDaoImpl")
public class TrafficTypeDaoImpl extends BaseDaoImpl<TrafficType, Long> 
implements TrafficTypeDao{

}