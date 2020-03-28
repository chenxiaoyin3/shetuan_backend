package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ProfitShareConfirmDetailDao;
import com.hongyu.entity.ProfitShareConfirmDetail;
import com.hongyu.service.ProfitShareConfirmDetailService;

@Service("profitShareConfirmDetailServiceImpl")
public class ProfitShareConfirmDetailServiceImpl extends BaseServiceImpl<ProfitShareConfirmDetail, Long>
		implements ProfitShareConfirmDetailService {
	@Resource(name = "profitShareConfirmDetailDaoImpl")
	ProfitShareConfirmDetailDao dao;

	@Resource(name = "profitShareConfirmDetailDaoImpl")
	public void setBaseDao(ProfitShareConfirmDetailDao dao) {
		super.setBaseDao(dao);
	}
}