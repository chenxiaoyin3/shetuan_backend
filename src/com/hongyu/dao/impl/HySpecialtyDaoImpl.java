package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HySpecialtyLabelDao;
import com.hongyu.entity.HySpecialtyLabel;

@Repository("hySpecialtyDaoImpl")
public class HySpecialtyDaoImpl extends BaseDaoImpl<HySpecialtyLabel, Long> implements HySpecialtyLabelDao{


}
