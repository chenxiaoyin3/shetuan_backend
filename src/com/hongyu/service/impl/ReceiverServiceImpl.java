package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.Receiver;
import com.hongyu.service.ReceiverService;

@Service("receiverServiceImpl")
public class ReceiverServiceImpl extends BaseServiceImpl<Receiver, Long> implements ReceiverService{

	@Override
	@Resource(name="receiverDaoImpl")
	public void setBaseDao(BaseDao<Receiver, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
