package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayablesBranchsettleRefundDao;
import com.hongyu.entity.PayablesBranchsettleRefund;
import com.hongyu.service.PayablesBranchsettleRefundService;

@Service(value = "payablesBranchsettleRefundServiceImpl")
public class PayablesBranchsettleRefundServiceImpl extends BaseServiceImpl<PayablesBranchsettleRefund,Long>
    implements PayablesBranchsettleRefundService {
	@Resource(name = "payablesBranchsettleRefundDaoImpl")
	PayablesBranchsettleRefundDao dao;

	@Resource(name = "payablesBranchsettleRefundDaoImpl")
	public void setBaseDao(PayablesBranchsettleRefundDao dao) {
		super.setBaseDao(dao);
	}
}
