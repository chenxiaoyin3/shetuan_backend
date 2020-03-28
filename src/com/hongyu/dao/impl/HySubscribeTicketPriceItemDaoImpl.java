package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HySubscribeTicketPriceItemDao;
import com.hongyu.entity.HySubscribeTicketPriceItem;

@Repository("hySubscribeTicketPriceItemDaoImpl")
public class HySubscribeTicketPriceItemDaoImpl extends BaseDaoImpl<HySubscribeTicketPriceItem, Long>
		implements HySubscribeTicketPriceItemDao {
}