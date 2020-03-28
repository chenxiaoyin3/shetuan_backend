package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HySubscribeTicketPriceItemDao;
import com.hongyu.entity.HySubscribeTicketPriceItem;
import com.hongyu.service.HySubscribeTicketPriceItemService;

@Service("hySubscribeTicketPriceItemServiceImpl")
public class HySubscribeTicketPriceItemServiceImpl extends BaseServiceImpl<HySubscribeTicketPriceItem, Long>
		implements HySubscribeTicketPriceItemService {
	@Resource(name = "hySubscribeTicketPriceItemDaoImpl")
	HySubscribeTicketPriceItemDao dao;

	@Resource(name = "hySubscribeTicketPriceItemDaoImpl")
	public void setBaseDao(HySubscribeTicketPriceItemDao dao) {
		super.setBaseDao(dao);
	}
}