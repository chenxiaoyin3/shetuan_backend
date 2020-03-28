package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.FddDayTripContractDao;
import com.hongyu.entity.FddDayTripContract;
@Repository("fddDayTripContractDaoImpl")
public class FddDayTripContractDaoImpl extends BaseDaoImpl<FddDayTripContract, Long> implements FddDayTripContractDao{
	
}
