package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GuideReviewFormScoreDao;
import com.hongyu.entity.GuideReviewFormScore;

@Repository("guideReviewFormScoreDaoImpl")
public class GuideReviewFormScoreDaoImpl extends BaseDaoImpl<GuideReviewFormScore, Long> implements GuideReviewFormScoreDao {

}
