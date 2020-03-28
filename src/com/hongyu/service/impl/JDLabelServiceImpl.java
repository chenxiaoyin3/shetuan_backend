package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.JDLabel;
import com.hongyu.service.JDLabelService;


@Service("jdLabelServiceImpl")
public class JDLabelServiceImpl extends BaseServiceImpl<JDLabel, Long> implements JDLabelService{

	@Override
	@Resource(name = "jdLabelDaoImpl")
	public void setBaseDao(BaseDao<JDLabel,Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
}
