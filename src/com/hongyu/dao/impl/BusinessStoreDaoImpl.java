package com.hongyu.dao.impl;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessStoreDao;
import com.hongyu.entity.BusinessStore;
import org.springframework.stereotype.Repository;

@Repository("businessStoreDaoImpl")
public class BusinessStoreDaoImpl
  extends BaseDaoImpl<BusinessStore, Long>
  implements BusinessStoreDao
{}
