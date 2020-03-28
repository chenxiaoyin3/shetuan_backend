package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyCompanyDao;
import com.hongyu.entity.HyCompany;
@Repository("hyCompanyDaoImpl")
public class HyCompanyDaoImpl extends BaseDaoImpl<HyCompany, Long> implements HyCompanyDao {

}
