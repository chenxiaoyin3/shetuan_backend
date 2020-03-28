package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ProfitShareWechatBusinessDao;
import com.hongyu.entity.ProfitShareWechatBusiness;
import com.hongyu.service.ProfitShareWechatBusinessService;

@Service("profitShareWechatBusinessServiceImpl")
public class ProfitShareWechatBusinessServiceImpl extends BaseServiceImpl<ProfitShareWechatBusiness, Long>
		implements ProfitShareWechatBusinessService {
	@Resource(name = "profitShareWechatBusinessDaoImpl")
	ProfitShareWechatBusinessDao dao;

	@Resource(name = "profitShareWechatBusinessDaoImpl")
	public void setBaseDao(ProfitShareWechatBusinessDao dao) {
		super.setBaseDao(dao);
	}
}