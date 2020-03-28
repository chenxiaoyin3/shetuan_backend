package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.InsuranceMonthDao;
import com.hongyu.entity.InsuranceMonth;

@Repository("insuranceMonthDaoImpl")
public class InsuranceMonthDaoImpl extends BaseDaoImpl<InsuranceMonth, Long> implements InsuranceMonthDao {

}
