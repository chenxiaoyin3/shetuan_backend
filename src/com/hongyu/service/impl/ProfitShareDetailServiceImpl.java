package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ProfitShareDetailDao;
import com.hongyu.entity.ProfitShareDetail;
import com.hongyu.service.ProfitShareDetailService;

@Service("profitShareDetailServiceImpl")
public class ProfitShareDetailServiceImpl extends BaseServiceImpl<ProfitShareDetail, Long>
		implements ProfitShareDetailService {
	@Resource(name = "profitShareDetailDaoImpl")
	ProfitShareDetailDao dao;

	@Resource(name = "profitShareDetailDaoImpl")
	public void setBaseDao(ProfitShareDetailDao dao) {
		super.setBaseDao(dao);
	}
}