package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BankAccountTypeDao;
import com.hongyu.entity.BankAccountType;

@Repository("bankAccountTypeDaoImpl")
public class BankAccountTypeDaoImpl extends BaseDaoImpl<BankAccountType, Long> 
implements BankAccountTypeDao{

}
