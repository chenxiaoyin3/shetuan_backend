package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyServiceFeeNoncarDao;
import com.hongyu.entity.HyServiceFeeNoncar;

@Repository("hyServiceFeeNoncarDaoImpl")
public class HyServiceFeeNoncarDaoImpl extends BaseDaoImpl<HyServiceFeeNoncar,Integer> implements HyServiceFeeNoncarDao{

}
