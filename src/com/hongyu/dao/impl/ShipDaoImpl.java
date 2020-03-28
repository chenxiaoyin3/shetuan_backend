package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ShipDao;
import com.hongyu.entity.Ship;
@Repository("shipDaoImpl")
public class ShipDaoImpl extends BaseDaoImpl<Ship, Long> implements ShipDao {

}
