package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.QuestionnaireEntryDao;
import com.hongyu.entity.QuestionnaireEntry;

@Repository("questionnaireEntryDaoImpl")
public class QuestionnaireEntryDaoImpl extends BaseDaoImpl<QuestionnaireEntry, Long> implements QuestionnaireEntryDao {

}
