package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.dao.impl.BaseDaoImpl;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyDepotAdmin;
import com.hongyu.service.HyDepotAdminService;

@Service("hyDepotAdminServiceImpl")
public class HyDepotAdminServiceImpl extends BaseServiceImpl<HyDepotAdmin, Long> implements HyDepotAdminService {
	
	@Resource(name="hyDepotAdminDaoImpl")
	public void setBaseDao(BaseDao dao){
		super.setBaseDao(dao);
	}

}
