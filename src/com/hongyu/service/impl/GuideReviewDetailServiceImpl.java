package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.GuideReviewDetail;
import com.hongyu.service.GuideReviewDetailService;

@Service("guideReviewDetailServiceImpl")
public class GuideReviewDetailServiceImpl extends BaseServiceImpl<GuideReviewDetail,Long> implements GuideReviewDetailService{

	@Override
	@Resource(name="guideReviewDetailDaoImpl")
	public void setBaseDao(BaseDao<GuideReviewDetail, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
