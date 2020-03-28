package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReviewFormItemDao;
import com.hongyu.entity.ReviewFormItem;

@Repository("reviewFormItemDaoImpl")
public class ReviewFormItemDaoImpl extends BaseDaoImpl<ReviewFormItem,Long> implements ReviewFormItemDao{

}
