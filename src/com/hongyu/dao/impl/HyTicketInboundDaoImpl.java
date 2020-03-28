package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyTicketInboundDao;
import com.hongyu.entity.HyTicketInbound;

@Repository("hyTicketInboundDaoImpl")
public class HyTicketInboundDaoImpl extends BaseDaoImpl<HyTicketInbound,Long> implements HyTicketInboundDao {

}
