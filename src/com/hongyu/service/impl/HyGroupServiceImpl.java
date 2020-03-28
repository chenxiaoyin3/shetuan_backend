package com.hongyu.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyGroupDao;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.service.HyGroupService;
@Service(value = "hyGroupServiceImpl")
public class HyGroupServiceImpl extends BaseServiceImpl<HyGroup, Long> implements HyGroupService {
	@Resource(name = "hyGroupDaoImpl")
	HyGroupDao dao;
	
	@Resource(name = "hyGroupDaoImpl")
	public void setBaseDao(HyGroupDao dao){
		super.setBaseDao(dao);		
	}
	
	@Transactional(readOnly = true)
	public boolean groupDayExist(Date startDay, HyLine line, Long id,Boolean teamType) {	
		List<Date> days = dao.groupDateExist(line.getId(), id,teamType);
		for(Date d : days) {
			if(DateUtils.isSameDay(startDay, d)) {
				return true;
			}
		}
		return false;				
	}
}
