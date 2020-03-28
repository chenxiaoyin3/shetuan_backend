package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GuideSettlementDetailDao;
import com.hongyu.entity.GuideSettlementDetail;

@Repository("guideSettlementDetailDaoImpl")
public class GuideSettlementDetailDaoImpl extends BaseDaoImpl<GuideSettlementDetail, Long> implements GuideSettlementDetailDao {

}
