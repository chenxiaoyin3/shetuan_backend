package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.InboundDao;
import com.hongyu.entity.Inbound;

@Repository("inboundDaoImpl")
public class InboundDaoImpl extends BaseDaoImpl<Inbound, Long> implements InboundDao {

}
