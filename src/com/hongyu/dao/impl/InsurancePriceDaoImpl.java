package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.InsurancePriceDao;
import com.hongyu.entity.InsurancePrice;

@Repository("insurancePriceDaoImpl")
public class InsurancePriceDaoImpl extends BaseDaoImpl<InsurancePrice, Long> implements InsurancePriceDao {

}
