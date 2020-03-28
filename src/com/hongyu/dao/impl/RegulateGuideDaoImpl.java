package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.RegulateGuideDao;
import com.hongyu.entity.RegulateGuide;
@Repository("regulateGuideDaoImpl")
public class RegulateGuideDaoImpl extends BaseDaoImpl<RegulateGuide, Long> implements RegulateGuideDao {

}
