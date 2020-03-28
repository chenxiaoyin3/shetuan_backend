package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ProfitShareDetailDao;
import com.hongyu.entity.ProfitShareDetail;

@Repository("profitShareDetailDaoImpl")
public class ProfitShareDetailDaoImpl extends BaseDaoImpl<ProfitShareDetail, Long> implements ProfitShareDetailDao {
}