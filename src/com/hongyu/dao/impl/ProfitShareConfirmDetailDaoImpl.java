package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ProfitShareConfirmDetailDao;
import com.hongyu.entity.ProfitShareConfirmDetail;

@Repository("profitShareConfirmDetailDaoImpl")
public class ProfitShareConfirmDetailDaoImpl extends BaseDaoImpl<ProfitShareConfirmDetail, Long>
		implements ProfitShareConfirmDetailDao {
}