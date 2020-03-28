package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.FddContractDao;
import com.hongyu.entity.FddContract;
@Repository("fddContractDaoImpl")
public class FddContractDaoImpl extends BaseDaoImpl<FddContract, Long>	implements FddContractDao{

}
