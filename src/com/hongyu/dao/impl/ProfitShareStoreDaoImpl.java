package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ProfitShareStoreDao;
import com.hongyu.entity.ProfitShareStore;

@Repository("profitShareStoreDaoImpl")
public class ProfitShareStoreDaoImpl extends BaseDaoImpl<ProfitShareStore, Long> implements ProfitShareStoreDao {
}