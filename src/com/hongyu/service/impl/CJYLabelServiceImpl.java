package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.CJYLabel;
import com.hongyu.service.CJYLabelService;

@Service("cjyLabelServiceImpl")
public class CJYLabelServiceImpl extends BaseServiceImpl<CJYLabel, Long> implements CJYLabelService{

	@Override
	@Resource(name = "cjyLabelDaoImpl")
	public void setBaseDao(BaseDao<CJYLabel,Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
}