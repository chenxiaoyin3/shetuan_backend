package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.AccountDao;
import com.hongyu.entity.Account;

@Repository("accountDaoImpl")
public class AccountDaoImpl extends BaseDaoImpl<Account, Long> implements AccountDao {
}