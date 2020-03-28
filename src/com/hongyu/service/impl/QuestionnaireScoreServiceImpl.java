package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.QuestionnaireScore;
import com.hongyu.service.QuestionnaireScoreService;

@Service("questionnaireScoreServiceImpl")
public class QuestionnaireScoreServiceImpl extends BaseServiceImpl<QuestionnaireScore, Long > implements QuestionnaireScoreService {

	@Override
	@Resource(name="questionnaireScoreDaoImpl")
	public void setBaseDao(BaseDao<QuestionnaireScore, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
