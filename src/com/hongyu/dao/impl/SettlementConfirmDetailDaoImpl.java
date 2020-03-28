package com.hongyu.dao.impl;import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SettlementConfirmDetailDao;
import com.hongyu.entity.SettlementConfirmDetail;@Repository("settlementConfirmDetailDaoImpl")public class SettlementConfirmDetailDaoImpl extends BaseDaoImpl<SettlementConfirmDetail, Long> implements SettlementConfirmDetailDao {}