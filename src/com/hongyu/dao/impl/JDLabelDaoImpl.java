package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.JDLabelDao;
import com.hongyu.entity.JDLabel;
@Repository("jdLabelDaoImpl")
public class JDLabelDaoImpl extends BaseDaoImpl<JDLabel, Long> implements JDLabelDao{

}
