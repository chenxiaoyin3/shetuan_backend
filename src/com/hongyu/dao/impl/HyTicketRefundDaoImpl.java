package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyTicketRefundDao;
import com.hongyu.entity.HyTicketRefund;

@Repository("hyTicketRefundDaoImpl")
public class HyTicketRefundDaoImpl extends BaseDaoImpl<HyTicketRefund,Long> implements HyTicketRefundDao {

}
