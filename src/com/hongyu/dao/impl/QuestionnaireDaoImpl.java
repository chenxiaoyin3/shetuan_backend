package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.QuestionnaireDao;
import com.hongyu.entity.Questionnaire;

@Repository("questionnaireDaoImpl")
public class QuestionnaireDaoImpl extends BaseDaoImpl<Questionnaire, Long> implements QuestionnaireDao {

}
