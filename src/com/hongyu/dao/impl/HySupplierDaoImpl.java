package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HySupplierDao;
import com.hongyu.entity.HySupplier;
@Repository("hySupplierDaoImpl")
public class HySupplierDaoImpl extends BaseDaoImpl<HySupplier, Long> implements HySupplierDao {

}
