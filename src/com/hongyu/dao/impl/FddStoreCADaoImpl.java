package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.FddStoreCADao;
import com.hongyu.entity.FddStoreCA;
@Repository("fddStoreCADaoImpl")
public class FddStoreCADaoImpl extends BaseDaoImpl<FddStoreCA, Long> implements FddStoreCADao{

}
