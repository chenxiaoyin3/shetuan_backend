package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.Questionnaire;
import com.hongyu.service.QuestionnaireService;

@Service("questionnaireServiceImpl")
public class QuestionnaireServiceImpl extends BaseServiceImpl<Questionnaire,Long > implements QuestionnaireService {

	@Override
	@Resource(name="questionnaireDaoImpl")
	public void setBaseDao(BaseDao<Questionnaire, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
