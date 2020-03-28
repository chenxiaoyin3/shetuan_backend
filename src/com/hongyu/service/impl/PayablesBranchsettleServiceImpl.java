package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayablesBranchsettleDao;
import com.hongyu.entity.PayablesBranchsettle;
import com.hongyu.service.PayablesBranchsettleService;

@Service("payablesBranchsettleServiceImpl")
public class PayablesBranchsettleServiceImpl extends BaseServiceImpl<PayablesBranchsettle,Long>
implements PayablesBranchsettleService{
	@Resource(name = "payablesBranchsettleDaoImpl")
	PayablesBranchsettleDao dao;

	@Resource(name = "payablesBranchsettleDaoImpl")
	public void setBaseDao(PayablesBranchsettleDao dao) {
		super.setBaseDao(dao);
	}
}
