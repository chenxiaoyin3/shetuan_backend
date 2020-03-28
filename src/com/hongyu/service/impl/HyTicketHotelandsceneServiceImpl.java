package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketHotelandsceneDao;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.service.HyTicketHotelandsceneService;

@Service("hyTicketHotelandsceneServiceImpl")
public class HyTicketHotelandsceneServiceImpl extends BaseServiceImpl<HyTicketHotelandscene,Long>
    implements HyTicketHotelandsceneService{
	@Resource(name = "hyTicketHotelandsceneDaoImpl")
	HyTicketHotelandsceneDao dao;
	
	@Resource(name = "hyTicketHotelandsceneDaoImpl")
	public void setBaseDao(HyTicketHotelandsceneDao dao){
		super.setBaseDao(dao);		
	}	
}
