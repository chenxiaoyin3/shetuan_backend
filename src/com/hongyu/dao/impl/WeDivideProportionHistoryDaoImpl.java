package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.WeDivideProportionHistoryDao;
import com.hongyu.entity.WeDivideProportionHistory;

@Repository("weDivideProportionHistoryDaoImpl")
public class WeDivideProportionHistoryDaoImpl extends BaseDaoImpl<WeDivideProportionHistory, Long> implements WeDivideProportionHistoryDao {

}
