package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ProfitShareConfirmDao;
import com.hongyu.entity.ProfitShareConfirm;

@Repository("profitShareConfirmDaoImpl")
public class ProfitShareConfirmDaoImpl extends BaseDaoImpl<ProfitShareConfirm, Long> implements ProfitShareConfirmDao {
}