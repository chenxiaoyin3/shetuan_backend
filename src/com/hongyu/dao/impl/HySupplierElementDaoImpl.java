package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HySupplierElementDao;
import com.hongyu.entity.HySupplierElement;
@Repository("hySupplierElementDaoImpl")
public class HySupplierElementDaoImpl extends BaseDaoImpl<HySupplierElement, Long> implements HySupplierElementDao {

}
