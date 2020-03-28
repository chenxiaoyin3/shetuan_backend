package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyPaymentSupplierDao;
import com.hongyu.entity.HyPaymentSupplier;

@Repository("hyPaymentSupplierDaoImpl")
public class HyPaymentSupplierDaoImpl extends BaseDaoImpl<HyPaymentSupplier,Long> implements HyPaymentSupplierDao{

}
