package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayablesBranchsettleRefundDao;
import com.hongyu.entity.PayablesBranchsettleRefund;

@Repository("payablesBranchsettleRefundDaoImpl")
public class PayablesBranchsettleRefundDaoImpl extends BaseDaoImpl<PayablesBranchsettleRefund,Long>
implements PayablesBranchsettleRefundDao{

}
