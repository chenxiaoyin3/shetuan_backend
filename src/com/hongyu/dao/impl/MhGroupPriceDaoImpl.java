package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MhGroupPriceDao;
import com.hongyu.entity.MhGroupPrice;
@Repository("mhGroupPriceDaoImpl")
public class MhGroupPriceDaoImpl extends BaseDaoImpl<MhGroupPrice, Long> implements MhGroupPriceDao{

}
