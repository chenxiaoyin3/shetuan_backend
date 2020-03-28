package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyLineRefundDao;
import com.hongyu.entity.HyLineRefund;
@Repository("hyLineRefundDaoImpl")
public class HyLineRefundDaoImpl extends BaseDaoImpl<HyLineRefund, Long> implements HyLineRefundDao {

}
