package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BankListDao;
import com.hongyu.entity.BankList;
@Repository("bankListDaoImpl")
public class BankListDaoImpl extends BaseDaoImpl<BankList, Long> 
implements BankListDao{

}
