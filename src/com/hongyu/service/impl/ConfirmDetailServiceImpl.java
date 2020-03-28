package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ConfirmDetailDao;
import com.hongyu.entity.ConfirmDetail;
import com.hongyu.service.ConfirmDetailService;

@Service("confirmDetailServiceImpl")
public class ConfirmDetailServiceImpl extends BaseServiceImpl<ConfirmDetail, Long> implements ConfirmDetailService {
	@Resource(name = "confirmDetailDaoImpl")
	ConfirmDetailDao dao;

	@Resource(name = "confirmDetailDaoImpl")
	public void setBaseDao(ConfirmDetailDao dao) {
		super.setBaseDao(dao);
	}
}