package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.StoreDao;
import com.hongyu.entity.Store;

@Repository("storeDaoImpl")
public class StoreDaoImpl extends BaseDaoImpl<Store, Long> implements StoreDao {

}
