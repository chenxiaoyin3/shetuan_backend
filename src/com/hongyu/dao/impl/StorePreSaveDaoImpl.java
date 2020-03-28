package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.StorePreSaveDao;
import com.hongyu.entity.StorePreSave;

@Repository("storePreSaveDaoImpl")
public class StorePreSaveDaoImpl extends BaseDaoImpl<StorePreSave, Long> implements StorePreSaveDao {

}
