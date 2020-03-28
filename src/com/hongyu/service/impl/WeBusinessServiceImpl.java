package com.hongyu.service.impl;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.WeBusinessDao;
import com.hongyu.entity.WeBusiness;
import com.hongyu.service.WeBusinessService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("weBusinessServiceImpl")
public class WeBusinessServiceImpl extends BaseServiceImpl<WeBusiness, Long> implements WeBusinessService {
	@Resource(name = "weBusinessDaoImpl")
	WeBusinessDao weBusinessDao;

	@Override
	@Resource(name = "weBusinessDaoImpl")
	public void setBaseDao(BaseDao<WeBusiness, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
}
