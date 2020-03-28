package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HySubscribeTicketPriceDao;
import com.hongyu.entity.HySubscribeTicketPrice;

@Repository("hySubscribeTicketPriceDaoImpl")
public class HySubscribeTicketPriceDaoImpl extends BaseDaoImpl<HySubscribeTicketPrice,Long> implements HySubscribeTicketPriceDao {

}
