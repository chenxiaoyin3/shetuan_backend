package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessOrderDepotDao;
import com.hongyu.entity.BusinessOrderDepot;
@Repository("businessOrderDepotDaoImpl")
public class BusinessOrderDepotDaoImpl extends BaseDaoImpl<BusinessOrderDepot, Long> implements BusinessOrderDepotDao {

}
