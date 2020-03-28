package com.hongyu.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyAreaDao;
import com.hongyu.entity.HyArea;
import com.hongyu.service.HyAreaService;

@Service(value = "hyAreaServiceImpl")
public class HyAreaServiceImpl extends BaseServiceImpl<HyArea, Long> 
implements HyAreaService{
	@Resource(name = "hyAreaDaoImpl")
	HyAreaDao dao;
	
	@Resource(name = "hyAreaDaoImpl")
	public void setBaseDao(HyAreaDao dao){
		super.setBaseDao(dao);		
	}

}
