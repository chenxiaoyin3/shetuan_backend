package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GuideReviewFormDao;
import com.hongyu.entity.GuideReviewForm;

@Repository("guideReviewFormDaoImpl")
public class GuideReviewFormDaoImpl extends BaseDaoImpl<GuideReviewForm, Long> implements GuideReviewFormDao  {

}
