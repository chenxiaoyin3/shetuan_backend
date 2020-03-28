package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.InsuranceOrderDao;
import com.hongyu.entity.InsuranceOrder;

@Repository("insuranceOrderDaoImpl")
public class InsuranceOrderDaoImpl extends BaseDaoImpl<InsuranceOrder, Long> implements InsuranceOrderDao{

}
