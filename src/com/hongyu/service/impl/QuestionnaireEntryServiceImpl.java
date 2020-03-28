package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.QuestionnaireEntry;
import com.hongyu.service.QuestionnaireEntryService;

@Service("questionnaireEntrySerbiceImpl")
public class QuestionnaireEntryServiceImpl extends BaseServiceImpl<QuestionnaireEntry,Long> implements QuestionnaireEntryService {

	@Override
	@Resource(name="questionnaireEntryDaoImpl")
	public void setBaseDao(BaseDao<QuestionnaireEntry, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
