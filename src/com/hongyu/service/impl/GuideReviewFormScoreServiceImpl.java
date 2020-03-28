package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.GuideReviewFormScore;
import com.hongyu.service.GuideReviewFormScoreService;

@Service("guideReviewFormScoreServiceImpl")
public class GuideReviewFormScoreServiceImpl extends BaseServiceImpl<GuideReviewFormScore, Long> implements GuideReviewFormScoreService {

	@Override
	@Resource(name="guideReviewFormScoreDaoImpl")
	public void setBaseDao(BaseDao<GuideReviewFormScore, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
