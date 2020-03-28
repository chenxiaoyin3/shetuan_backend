package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketHotelRoomDao;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.service.HyTicketHotelRoomService;

@Service("hyTicketHotelRoomServiceImpl")
public class HyTicketHotelRoomServiceImpl extends BaseServiceImpl<HyTicketHotelRoom,Long> implements HyTicketHotelRoomService {
	@Resource(name = "hyTicketHotelRoomDaoImpl")
	HyTicketHotelRoomDao dao;
	
	@Resource(name = "hyTicketHotelRoomDaoImpl")
	public void setBaseDao(HyTicketHotelRoomDao dao){
		super.setBaseDao(dao);		
	}	
}
