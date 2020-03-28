package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyBusinessPVDao;
import com.hongyu.entity.HyBusinessPV;

@Repository("hyBusinessPVDaoImpl")
public class HyBusinessPVDaoImpl extends BaseDaoImpl<HyBusinessPV, Long> implements HyBusinessPVDao {

}
