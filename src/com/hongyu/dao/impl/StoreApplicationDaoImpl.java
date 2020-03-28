package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.StoreApplicationDao;
import com.hongyu.entity.StoreApplication;

@Repository("storeApplicationDaoImpl")
public class StoreApplicationDaoImpl extends BaseDaoImpl<StoreApplication, Long> implements StoreApplicationDao {

}
