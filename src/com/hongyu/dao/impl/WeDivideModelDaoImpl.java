package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.WeDivideModelDao;
import com.hongyu.entity.WeDivideModel;

@Repository("weDivideModelDaoImpl")
public class WeDivideModelDaoImpl extends BaseDaoImpl<WeDivideModel, Long> implements WeDivideModelDao {

}
