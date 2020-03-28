package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.BaseDao;
import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.entity.WithDrawCash;

@Repository("withDrawCashDaoImpl")
public class WithDrawCashDaoImpl extends BaseDaoImpl<WithDrawCash, Long> implements BaseDao<WithDrawCash, Long>{
	
}
