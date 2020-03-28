package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReviewFormDao;
import com.hongyu.entity.ReviewForm;

@Repository("reviewFormDaoImpl")
public class ReviewFormDaoImpl extends BaseDaoImpl<ReviewForm,Long> implements ReviewFormDao {

}
