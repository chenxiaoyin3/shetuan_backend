package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HySubscribeTicketDao;
import com.hongyu.entity.HySubscribeTicket;

@Repository("hySubscribeTicketDaoImpl")
public class HySubscribeTicketDaoImpl extends BaseDaoImpl<HySubscribeTicket, Long> implements HySubscribeTicketDao {
}