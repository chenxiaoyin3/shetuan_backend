package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MhPwRefundRuleDao;
import com.hongyu.entity.MhPwRefundRule;

@Repository("mhPwRefundRuleDaoImpl")
public class MhPwRefundRuleDaoImpl extends BaseDaoImpl<MhPwRefundRule,Long> implements MhPwRefundRuleDao {

}
