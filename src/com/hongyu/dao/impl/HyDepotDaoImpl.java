package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyDepotDao;
import com.hongyu.entity.HyDepot;

@Repository("hyDepotDaoImpl")
public class HyDepotDaoImpl extends BaseDaoImpl<HyDepot, Long> implements HyDepotDao{

}
