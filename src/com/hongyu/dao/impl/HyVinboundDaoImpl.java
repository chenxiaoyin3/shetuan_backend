package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyVinboundDao;
import com.hongyu.entity.HyVinbound;

@Repository("hyVinboundDaoImpl")
public class HyVinboundDaoImpl extends BaseDaoImpl<HyVinbound, Long> implements HyVinboundDao {

}
