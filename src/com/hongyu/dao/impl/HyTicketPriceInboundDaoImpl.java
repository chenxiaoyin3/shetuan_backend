package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyTicketPriceInboundDao;
import com.hongyu.entity.HyTicketPriceInbound;

@Repository("hyTicketPriceInboundDaoImpl")
public class HyTicketPriceInboundDaoImpl extends BaseDaoImpl<HyTicketPriceInbound,Long> implements HyTicketPriceInboundDao {

}
