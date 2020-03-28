package com.hongyu.dao.impl;


import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SpecialtyPriceDao;
import com.hongyu.entity.SpecialtyPrice;

@Repository("specialtyPriceDaoImpl")
public class SpecialtyPriceDaoImpl extends BaseDaoImpl<SpecialtyPrice, Long> implements SpecialtyPriceDao {

}
