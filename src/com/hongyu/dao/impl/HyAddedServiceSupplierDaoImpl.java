package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyAddedServiceSupplierDao;
import com.hongyu.entity.HyAddedServiceSupplier;

@Repository("hyAddedServiceSupplierDaoImpl")
public class HyAddedServiceSupplierDaoImpl extends BaseDaoImpl<HyAddedServiceSupplier,Long> implements HyAddedServiceSupplierDao {

}
