package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.DepositServicerDao;
import com.hongyu.entity.DepositServicer;

@Repository("depositServicerDaoImpl")
public class DepositServicerDaoImpl extends BaseDaoImpl<DepositServicer, Long> implements DepositServicerDao {

}
