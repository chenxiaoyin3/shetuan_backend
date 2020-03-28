package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyTicketHotelDao;
import com.hongyu.entity.HyTicketHotel;

@Repository("hyTicketHotelDaoImpl")
public class HyTicketHotelDaoImpl extends BaseDaoImpl<HyTicketHotel,Long> implements HyTicketHotelDao {

}
