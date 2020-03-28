package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PriceTypeDao;
import com.hongyu.entity.PriceType;
@Repository("priceTypeDaoImpl")
public class PriceTypeDaoImpl extends BaseDaoImpl<PriceType, Long> 
implements PriceTypeDao {

}
