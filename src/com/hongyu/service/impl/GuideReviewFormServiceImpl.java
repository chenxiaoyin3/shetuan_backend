package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.GuideReviewForm;
import com.hongyu.service.GuideReviewFormService;

@Service("guideReviewFormServiceImpl")
public class GuideReviewFormServiceImpl extends BaseServiceImpl<GuideReviewForm,Long> implements GuideReviewFormService{

	@Override
	@Resource(name="guideReviewFormDaoImpl")
	public void setBaseDao(BaseDao<GuideReviewForm, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
