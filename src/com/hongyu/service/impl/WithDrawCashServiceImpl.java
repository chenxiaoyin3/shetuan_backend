package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.WithDrawCash;
import com.hongyu.service.WithDrawCashService;

@Service("withDrawCashServiceImpl")
public class WithDrawCashServiceImpl extends BaseServiceImpl<WithDrawCash,Long> implements WithDrawCashService{
	@Override
	@Resource(name="withDrawCashDaoImpl")
	public void setBaseDao(BaseDao<WithDrawCash, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
}
