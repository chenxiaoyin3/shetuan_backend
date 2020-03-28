package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.GuideEdit;
import com.hongyu.service.GuideEditService;
@Service("guideEditServiceImpl")
public class GuideEditServiceImpl extends BaseServiceImpl<GuideEdit,Long> implements GuideEditService{

	@Override
	@Resource(name="guideEditDaoImpl")
	public void setBaseDao(BaseDao<GuideEdit, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
