package com.hongyu.dao.impl;import org.springframework.stereotype.Repository;import com.grain.dao.impl.BaseDaoImpl;import com.hongyu.dao.BalanceDueApplyDao;import com.hongyu.entity.BalanceDueApply;@Repository("balanceDueApplyDaoImpl")public class BalanceDueApplyDaoImpl extends BaseDaoImpl<BalanceDueApply, Long> implements BalanceDueApplyDao {}