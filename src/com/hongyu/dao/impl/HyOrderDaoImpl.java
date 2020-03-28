package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyOrderDao;
import com.hongyu.entity.HyOrder;

@Repository("hyOrderDaoImpl")
public class HyOrderDaoImpl extends BaseDaoImpl<HyOrder, Long> implements HyOrderDao {

}
