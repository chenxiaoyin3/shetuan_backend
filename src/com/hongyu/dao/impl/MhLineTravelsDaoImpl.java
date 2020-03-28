package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MhLineTravelsDao;
import com.hongyu.entity.MhLineTravels;
@Repository("mhLineTravelsDaoImpl")
public class MhLineTravelsDaoImpl extends BaseDaoImpl<MhLineTravels, Long> implements MhLineTravelsDao{

}
