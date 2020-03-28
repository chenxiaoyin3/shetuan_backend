package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyLabelDao;
import com.hongyu.entity.HyLabel;

@Repository("hyLabelDaoImpl")
public class HyLabelDaoImpl extends BaseDaoImpl<HyLabel, Long> implements HyLabelDao{

}
