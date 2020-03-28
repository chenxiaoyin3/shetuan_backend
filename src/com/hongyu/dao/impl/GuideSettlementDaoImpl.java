package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GuideSettlementDao;
import com.hongyu.entity.GuideSettlement;

@Repository("guideSettlementDaoImpl")
public class GuideSettlementDaoImpl extends BaseDaoImpl<GuideSettlement, Long> implements GuideSettlementDao {

}
