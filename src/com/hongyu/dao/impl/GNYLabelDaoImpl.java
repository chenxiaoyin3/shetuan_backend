package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GNYLabelDao;
import com.hongyu.entity.GNYLabel;
@Repository("gnyLabelDaoImpl")
public class GNYLabelDaoImpl extends BaseDaoImpl<GNYLabel, Long> implements GNYLabelDao{

}
