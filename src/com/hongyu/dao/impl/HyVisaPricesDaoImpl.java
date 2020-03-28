package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyVisaPricesDao;
import com.hongyu.entity.HyVisaPrices;

@Repository("hyVisaPricesDaoImpl")
public class HyVisaPricesDaoImpl extends BaseDaoImpl<HyVisaPrices, Long> implements HyVisaPricesDao{

}
