package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyLineTravelsDao;
import com.hongyu.entity.HyLineTravels;
@Repository("hyLineTravelsDaoImpl")
public class HyLineTravelsDaoImpl extends BaseDaoImpl<HyLineTravels, Long> implements HyLineTravelsDao {

}
