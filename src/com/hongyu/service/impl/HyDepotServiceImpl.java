package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyDepotDao;
import com.hongyu.entity.HyDepot;
import com.hongyu.service.HyDepotService;

@Service("hyDepotServiceImpl")
public class HyDepotServiceImpl extends BaseServiceImpl<HyDepot, Long> implements HyDepotService{

	@Resource(name="hyDepotDaoImpl")
	private HyDepotDao hyDepotDao;
	
	@Resource(name="hyDepotDaoImpl")
	public void setBaseDao(HyDepotDao dao){
		super.setBaseDao(dao);
	}
}
