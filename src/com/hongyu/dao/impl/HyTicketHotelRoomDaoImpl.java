package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyTicketHotelRoomDao;
import com.hongyu.entity.HyTicketHotelRoom;

@Repository("hyTicketHotelRoomDaoImpl")
public class HyTicketHotelRoomDaoImpl extends BaseDaoImpl<HyTicketHotelRoom,Long> implements HyTicketHotelRoomDao {

}
