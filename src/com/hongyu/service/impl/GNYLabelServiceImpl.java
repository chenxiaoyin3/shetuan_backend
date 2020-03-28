package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.GNYLabel;
import com.hongyu.service.GNYLabelService;
@Service("gnyLabelServiceImpl")
public class GNYLabelServiceImpl extends BaseServiceImpl<GNYLabel, Long> implements GNYLabelService{

	@Override
	@Resource(name = "gnyLabelDaoImpl")
	public void setBaseDao(BaseDao<GNYLabel,Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
}
