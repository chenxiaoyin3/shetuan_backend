package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyServiceFeeCarDao;
import com.hongyu.entity.HyServiceFeeCar;
@Repository("hyServiceFeeCarDaoImpl")
public class HyServiceFeeCarDaoImpl extends BaseDaoImpl<HyServiceFeeCar,Integer> implements HyServiceFeeCarDao{

}
