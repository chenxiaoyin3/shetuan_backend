package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyRegulateDao;
import com.hongyu.entity.HyRegulate;

@Repository("hyRegulateDaoImpl")
public class HyRegulateDaoImpl extends BaseDaoImpl<HyRegulate,Long> implements HyRegulateDao{

}
