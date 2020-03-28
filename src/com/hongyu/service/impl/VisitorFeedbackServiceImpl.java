package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.VisitorFeedback;
import com.hongyu.service.VisitorFeedbackService;

@Service("visitorFeedbackServiceImpl")
public class VisitorFeedbackServiceImpl extends BaseServiceImpl<VisitorFeedback,Long> implements VisitorFeedbackService {

	@Override
	@Resource(name="visitorFeedbackDaoImpl")
	public void setBaseDao(BaseDao<VisitorFeedback, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
