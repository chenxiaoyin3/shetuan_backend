package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.ExposeReply;
import com.hongyu.service.ExposeReplyService;

@Service("exposeReplyServiceImpl")
public class ExposeReplyServiceImpl extends BaseServiceImpl<ExposeReply, Long> implements ExposeReplyService {

	@Override
	@Resource(name="exposeReplyDaoImpl")
	public void setBaseDao(BaseDao<ExposeReply, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	
	
}
