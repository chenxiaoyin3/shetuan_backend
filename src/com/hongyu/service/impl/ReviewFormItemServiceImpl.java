package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.ReviewFormItem;
import com.hongyu.service.ReviewFormItemService;

@Service("reviewFormItemServiceImpl")
public class ReviewFormItemServiceImpl extends BaseServiceImpl<ReviewFormItem,Long> implements ReviewFormItemService {

	@Override
	@Resource(name="reviewFormItemDaoImpl")
	public void setBaseDao(BaseDao<ReviewFormItem, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
