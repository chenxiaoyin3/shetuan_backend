package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.GuideSettlementDetail;
import com.hongyu.service.GuideSettlementDetailService;

@Service("guideSettlementDetailServiceImpl")
public class GuideSettlementDetailServiceImpl extends BaseServiceImpl<GuideSettlementDetail,Long> implements GuideSettlementDetailService {

	@Resource(name="guideSettlementDetailDaoImpl")
	@Override
	public void setBaseDao(BaseDao<GuideSettlementDetail, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
	
}
