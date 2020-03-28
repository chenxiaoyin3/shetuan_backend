package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayablesBranchsettleDao;
import com.hongyu.entity.PayablesBranchsettle;

@Repository("payablesBranchsettleDaoImpl")
public class PayablesBranchsettleDaoImpl extends BaseDaoImpl<PayablesBranchsettle,Long> implements PayablesBranchsettleDao {

}
