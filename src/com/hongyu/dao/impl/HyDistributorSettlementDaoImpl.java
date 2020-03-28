package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyDistributorSettlementDao;
import com.hongyu.entity.HyDistributorSettlement;

@Repository("hyDistributorSettlementDaoImpl")
public class HyDistributorSettlementDaoImpl extends BaseDaoImpl<HyDistributorSettlement,Long> implements HyDistributorSettlementDao{

}
