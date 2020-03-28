package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SpecialtyLostDao;
import com.hongyu.entity.SpecialtyLost;

@Repository("specialtyLostDaoImpl")
public class SpecialtyLostDaoImpl extends BaseDaoImpl<SpecialtyLost, Long> implements SpecialtyLostDao {

}
