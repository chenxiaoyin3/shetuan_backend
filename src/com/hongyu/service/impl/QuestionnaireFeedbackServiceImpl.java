package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.QuestionnaireFeedback;
import com.hongyu.service.QuestionnaireFeedbackService;

@Service("questionnaireFeedbackServiceImpl")
public class QuestionnaireFeedbackServiceImpl extends BaseServiceImpl<QuestionnaireFeedback,Long> implements QuestionnaireFeedbackService{

	@Override
	@Resource(name="questionnaireFeedbackDaoImpl")
	public void setBaseDao(BaseDao<QuestionnaireFeedback, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
