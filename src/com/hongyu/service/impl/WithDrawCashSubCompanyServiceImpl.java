package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.WithDrawCashSubCompany;
import com.hongyu.service.WithDrawCashSubCompanyService;

@Service("withDrawCashSubCompanyServiceImpl")
public class WithDrawCashSubCompanyServiceImpl extends BaseServiceImpl<WithDrawCashSubCompany,Long> implements WithDrawCashSubCompanyService {
	@Override
	@Resource(name="withDrawCashSubCompanyDaoImpl")
	public void setBaseDao(BaseDao<WithDrawCashSubCompany, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
}
