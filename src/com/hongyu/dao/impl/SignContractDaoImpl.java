package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SignContractDao;
import com.hongyu.entity.SignContract;

@Repository("signContractDaoImpl")
public class SignContractDaoImpl extends BaseDaoImpl<SignContract, Long> implements SignContractDao {

}
