package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.WeDivideModelHistoryDao;
import com.hongyu.entity.WeDivideModelHistory;

@Repository("weDivideModelHistoryDaoImpl")
public class WeDivideModelHistoryDaoImpl extends BaseDaoImpl<WeDivideModelHistory, Long> implements WeDivideModelHistoryDao {

}
