package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyTicketSceneDao;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.service.HyTicketSceneService;

@Service("hyTicketSceneServiceImpl")
public class HyTicketSceneServiceImpl extends BaseServiceImpl<HyTicketScene,Long>
        implements HyTicketSceneService {
	@Resource(name = "hyTicketSceneDaoImpl")
	HyTicketSceneDao dao;
	
	@Resource(name = "hyTicketSceneDaoImpl")
	public void setBaseDao(HyTicketSceneDao dao){
		super.setBaseDao(dao);		
	}	
}
