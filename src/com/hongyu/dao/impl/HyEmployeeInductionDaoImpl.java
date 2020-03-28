package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyEmployeeInductionDao;
import com.hongyu.entity.HyEmployeeInduction;

@Repository("hyEmployeeInductionDaoImpl")
public class HyEmployeeInductionDaoImpl extends BaseDaoImpl<HyEmployeeInduction,Long> implements HyEmployeeInductionDao{

}
