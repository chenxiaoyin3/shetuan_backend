package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyRegulateitemOrderDao;
import com.hongyu.entity.HyRegulateitemOrder;

@Repository("hyRegulateitemOrderDaoImpl")
public class HyRegulateitemOrderDaoImpl extends BaseDaoImpl<HyRegulateitemOrder,Long> implements HyRegulateitemOrderDao {

}
