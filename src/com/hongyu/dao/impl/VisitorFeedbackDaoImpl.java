package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.VisitorFeedbackDao;
import com.hongyu.entity.VisitorFeedback;
@Repository("visitorFeedbackDaoImpl")
public class VisitorFeedbackDaoImpl extends BaseDaoImpl<VisitorFeedback,Long> implements VisitorFeedbackDao{

}
