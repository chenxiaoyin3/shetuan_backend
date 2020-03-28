package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyTicketSubscribePriceDao;
import com.hongyu.entity.HyTicketSubscribePrice;

@Repository("hyTicketSubscribePriceDaoImpl")
public class HyTicketSubscribePriceDaoImpl extends BaseDaoImpl<HyTicketSubscribePrice,Long> implements HyTicketSubscribePriceDao {

}
