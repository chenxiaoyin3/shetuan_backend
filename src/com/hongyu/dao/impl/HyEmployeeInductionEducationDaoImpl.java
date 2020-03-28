package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyEmployeeInductionEducationDao;
import com.hongyu.entity.HyEmployeeInductionEducation;

@Repository("hyEmployeeInductionEducationDaoImpl")
public class HyEmployeeInductionEducationDaoImpl extends BaseDaoImpl<HyEmployeeInductionEducation,Long> implements HyEmployeeInductionEducationDao{

}
