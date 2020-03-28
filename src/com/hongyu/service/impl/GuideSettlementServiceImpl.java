package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.GuideSettlement;
import com.hongyu.service.GuideSettlementService;

@Service("guideSettlementServiceImpl")
public class GuideSettlementServiceImpl extends BaseServiceImpl<GuideSettlement,Long> implements GuideSettlementService {

	@Resource(name="guideSettlementDaoImpl")
	@Override
	public void setBaseDao(BaseDao<GuideSettlement, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
