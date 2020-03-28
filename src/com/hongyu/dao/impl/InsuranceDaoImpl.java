package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.InsuranceDao;
import com.hongyu.entity.Insurance;

@Repository("insuranceDaoImpl")
public class InsuranceDaoImpl extends BaseDaoImpl<Insurance, Long> implements InsuranceDao {

}
