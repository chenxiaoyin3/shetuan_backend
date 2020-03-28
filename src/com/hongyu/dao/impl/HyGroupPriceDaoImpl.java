package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyGroupPriceDao;
import com.hongyu.entity.HyGroupPrice;
@Repository("hyGroupPriceDaoImpl")
public class HyGroupPriceDaoImpl extends BaseDaoImpl<HyGroupPrice, Long> implements HyGroupPriceDao {

}
