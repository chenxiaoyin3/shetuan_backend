package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.WeDivideProportionDao;
import com.hongyu.entity.WeDivideProportion;

@Repository("weDivideProportionDaoImpl")
public class WeDivideProportionDaoImpl extends BaseDaoImpl<WeDivideProportion, Long> 
implements WeDivideProportionDao {

}
