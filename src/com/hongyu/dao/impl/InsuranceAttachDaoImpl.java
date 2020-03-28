package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.InsuranceAttachDao;
import com.hongyu.entity.InsuranceAttach;

@Repository("insuranceAttachDaoImpl")
public class InsuranceAttachDaoImpl extends BaseDaoImpl<InsuranceAttach, Long> implements InsuranceAttachDao {

}
