package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyTicketHotelandsceneDao;
import com.hongyu.entity.HyTicketHotelandscene;

@Repository("hyTicketHotelandsceneDaoImpl")
public class HyTicketHotelandsceneDaoImpl extends BaseDaoImpl<HyTicketHotelandscene,Long> implements HyTicketHotelandsceneDao {

}
