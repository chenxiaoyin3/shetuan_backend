package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.VisitorFeedbackScore;
import com.hongyu.service.VisitorFeedbackScoreService;

@Service("visitorFeedbackScoreServiceImpl")
public class VisitorFeedbackScoreServiceImpl extends BaseServiceImpl<VisitorFeedbackScore,Long> implements VisitorFeedbackScoreService{

	@Override
	@Resource(name="visitorFeedbackScoreDaoImpl")
	public void setBaseDao(BaseDao<VisitorFeedbackScore, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
