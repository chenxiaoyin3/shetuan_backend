package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.ZBYLabel;
import com.hongyu.service.ZBYLabelService;

@Service("zbyLabelServiceImpl")
public class ZBYLabelServiceImpl extends BaseServiceImpl<ZBYLabel, Long> implements ZBYLabelService{

	@Override
	@Resource(name = "zbyLabelDaoImpl")
	public void setBaseDao(BaseDao<ZBYLabel,Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
}
