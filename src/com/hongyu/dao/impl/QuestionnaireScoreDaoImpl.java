package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.QuestionnaireScoreDao;
import com.hongyu.entity.QuestionnaireScore;

@Repository("questionnaireScoreDaoImpl")
public class QuestionnaireScoreDaoImpl extends BaseDaoImpl<QuestionnaireScore, Long> implements QuestionnaireScoreDao {

}
