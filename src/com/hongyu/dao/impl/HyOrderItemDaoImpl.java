package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyOrderItemDao;
import com.hongyu.entity.HyOrderItem;

@Repository("hyOrderItemDaoImpl")
public class HyOrderItemDaoImpl extends BaseDaoImpl<HyOrderItem, Long> implements HyOrderItemDao {

}
