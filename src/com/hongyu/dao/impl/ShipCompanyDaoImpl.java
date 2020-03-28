package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ShipCompanyDao;
import com.hongyu.entity.ShipCompany;

@Repository("shipCompanyDaoImpl")
public class ShipCompanyDaoImpl extends BaseDaoImpl<ShipCompany,Long> implements ShipCompanyDao {

}
