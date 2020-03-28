package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MonthShouldPayInterTDao;
import com.hongyu.entity.MonthShouldPayInterT;

@Repository("monthShouldPayInterTDaoImpl")
public class MonthShouldPayInterTDaoImpl extends BaseDaoImpl<MonthShouldPayInterT,Long> implements MonthShouldPayInterTDao{

}
