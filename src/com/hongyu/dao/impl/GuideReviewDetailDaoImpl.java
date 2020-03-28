package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GuideReviewDetailDao;
import com.hongyu.entity.GuideReviewDetail;

@Repository("guideReviewDetailDaoImpl")
public class GuideReviewDetailDaoImpl extends BaseDaoImpl<GuideReviewDetail, Long> implements GuideReviewDetailDao {

}
