package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyTicketSubscribeDao;
import com.hongyu.entity.HyTicketSubscribe;

@Repository("hyTicketSubscribeDaoImpl")
public class HyTicketSubscribeDaoImpl extends BaseDaoImpl<HyTicketSubscribe,Long> implements HyTicketSubscribeDao {

}
