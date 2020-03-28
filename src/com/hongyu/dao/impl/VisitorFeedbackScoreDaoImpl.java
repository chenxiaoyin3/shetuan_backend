package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.VisitorFeedbackScoreDao;
import com.hongyu.entity.VisitorFeedbackScore;

@Repository("visitorFeedbackScoreDaoImpl")
public class VisitorFeedbackScoreDaoImpl extends BaseDaoImpl<VisitorFeedbackScore,Long> implements VisitorFeedbackScoreDao {

}
