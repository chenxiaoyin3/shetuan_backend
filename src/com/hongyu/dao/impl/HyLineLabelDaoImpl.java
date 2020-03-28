package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyLineLabelDao;
import com.hongyu.entity.HyLineLabel;

@Repository("hyLineLabelDaoImpl")
public class HyLineLabelDaoImpl extends BaseDaoImpl<HyLineLabel, Long> implements HyLineLabelDao{

}
