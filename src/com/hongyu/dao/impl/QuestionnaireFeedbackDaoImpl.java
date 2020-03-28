package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.QuestionnaireFeedbackDao;
import com.hongyu.entity.QuestionnaireFeedback;

@Repository("questionnaireFeedbackDaoImpl")
public class QuestionnaireFeedbackDaoImpl extends BaseDaoImpl<QuestionnaireFeedback, Long> implements QuestionnaireFeedbackDao {

}
