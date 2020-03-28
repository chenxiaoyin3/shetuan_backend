package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.InsuranceTimeDao;
import com.hongyu.entity.InsuranceTime;

@Repository("insuranceTimeDaoImpl")
public class InsuranceTimeDaoImpl extends BaseDaoImpl<InsuranceTime, Long> implements InsuranceTimeDao {

}
