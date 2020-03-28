package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyEmployeeInductionJobDao;
import com.hongyu.entity.HyEmployeeInductionJob;

@Repository("hyEmployeeInductionJobDaoImpl")
public class HyEmployeeInductionJobDaoImpl extends BaseDaoImpl<HyEmployeeInductionJob, Long> implements HyEmployeeInductionJobDao{

}
