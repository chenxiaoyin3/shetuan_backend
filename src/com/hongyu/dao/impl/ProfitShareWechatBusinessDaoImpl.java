package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ProfitShareWechatBusinessDao;
import com.hongyu.entity.ProfitShareWechatBusiness;

@Repository("profitShareWechatBusinessDaoImpl")
public class ProfitShareWechatBusinessDaoImpl extends BaseDaoImpl<ProfitShareWechatBusiness, Long>
		implements ProfitShareWechatBusinessDao {
}