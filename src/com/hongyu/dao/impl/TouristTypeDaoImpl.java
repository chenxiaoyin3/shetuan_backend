package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.TouristTypeDao;
import com.hongyu.entity.TouristType;
@Repository("touristTypeDaoImpl")
public class TouristTypeDaoImpl extends BaseDaoImpl<TouristType, Long> 
implements TouristTypeDao{

}
