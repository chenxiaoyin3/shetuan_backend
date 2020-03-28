package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HySupplierContractDao;
import com.hongyu.entity.HySupplierContract;
@Repository("hySupplierContractDaoImpl")
public class HySupplierContractDaoImpl extends BaseDaoImpl<HySupplierContract, Long> implements HySupplierContractDao {

}
