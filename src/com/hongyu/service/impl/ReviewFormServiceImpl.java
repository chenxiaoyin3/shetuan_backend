package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.ReviewForm;
import com.hongyu.service.ReviewFormService;

@Service("reviewFormServiceImpl")
public class ReviewFormServiceImpl extends BaseServiceImpl<ReviewForm,Long> implements ReviewFormService {

	@Resource(name="reviewFormDaoImpl")
	@Override
	public void setBaseDao(BaseDao<ReviewForm, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
