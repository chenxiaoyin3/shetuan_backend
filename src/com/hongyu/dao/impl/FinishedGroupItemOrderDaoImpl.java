package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.FinishedGroupItemOrderDao;
import com.hongyu.entity.FinishedGroupItemOrder;

@Repository("finishedGroupItemOrderDaoImpl")
public class FinishedGroupItemOrderDaoImpl extends BaseDaoImpl<FinishedGroupItemOrder, Long> implements FinishedGroupItemOrderDao{

}
