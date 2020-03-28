package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.WeightDao;
import com.hongyu.entity.Weight;
@Repository("weightDaoImpl")
public class WeightDaoImpl extends BaseDaoImpl<Weight,Long> implements WeightDao {

}
