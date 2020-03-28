package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MhLineRefundDao;
import com.hongyu.entity.MhLineRefund;
@Repository("mhLineRefundDaoImpl")
public class MhLineRefundDaoImpl extends BaseDaoImpl<MhLineRefund,Long> implements MhLineRefundDao{

}
