package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketHotelandsceneRoomDao;
import com.hongyu.entity.HyTicketHotelandsceneRoom;
import com.hongyu.service.HyTicketHotelandsceneRoomService;

@Service("hyTicketHotelandsceneRoomServiceImpl")
public class HyTicketHotelandsceneRoomServiceImpl extends BaseServiceImpl<HyTicketHotelandsceneRoom,Long>
implements HyTicketHotelandsceneRoomService{
	@Resource(name = "hyTicketHotelandsceneRoomDaoImpl")
	HyTicketHotelandsceneRoomDao dao;
	
	@Resource(name = "hyTicketHotelandsceneRoomDaoImpl")
	public void setBaseDao(HyTicketHotelandsceneRoomDao dao){
		super.setBaseDao(dao);		
	}	
}
